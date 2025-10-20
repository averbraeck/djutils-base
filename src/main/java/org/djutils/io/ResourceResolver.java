package org.djutils.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import org.djutils.exceptions.Throw;

/**
 * ResourceResolver resolves a resource on the classpath, on a file system, as a URL or in a jar-file. For resources, it looks
 * both in the root directory ad in the directory <code>/resources</code> as some Maven packaging stores the resources inside
 * this folder. The class can handle HTTP resources, also with username and password. Entries within jar-files can be retrieved
 * from any type of resource. Example of usage of the class:
 * 
 * <pre>
 * // Relative file (resolved against baseDir, but also against classpath and classpath/resources)
 * var h1 = ResourceResolver.resolve("data/input.csv");
 * 
 * // Absolute file
 * var h2 = ResourceResolver.resolve("/var/tmp/report.txt");
 * 
 * // HTTP(S)
 * var h3 = ResourceResolver.resolve("https://example.com/file.json");
 * 
 * // FTP with credentials
 * var h4 = ResourceResolver.resolve("ftp://user:pass@ftp.example.com/pub/notes.txt");
 * 
 * // Classpath with specific classloader and relative to a specific path
 * var h5 = ResourceResolver.resolve("config/app.yaml", MyApp.class.getClassLoader(), Path.of("."));
 * 
 * // Raw bang syntax -> normalized to jar:
 * var h6 = ResourceResolver.resolve("file:/opt/app/lib/app.jar!/META-INF/MANIFEST.MF");
 * 
 * // Use the result as a URL, URI, or Path:
 * try (var in = h6.openStream())
 * {
 *     // use h6 to read something from the stream
 * }
 * h6.asPath().ifPresent(path -> System.out.println(Files.size(path)));
 * System.out.println(h6.asUrl());
 * System.out.println(h6.asUri());
 * </pre>
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class ResourceResolver
{
    /** HTTP client for resolving http:// and https:// resources; build when needed. */
    private static HttpClient httpClient;

    /** Utility class constructor. */
    private ResourceResolver()
    {
        // utility class
    }

    /**
     * Resolve a resource string against a base directory and classloader.
     * @param resource the specification of the resource to load
     * @param classLoader the class loader to specifically use (can be null)
     * @param baseDirPath the potential base directory to which the resource is relative (can be null)
     * @param asResource read only relative to the classpath
     * @return a resource handle to the resource
     * @throws NoSuchElementException when the resource could not be found
     */
    private static ResourceHandle resolve(final String resource, final ClassLoader classLoader, final Path baseDirPath,
            final boolean asResource)
    {
        Throw.whenNull(resource, "resource");
        ClassLoader cl = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
        Path baseDir = baseDirPath == null ? Path.of("").toAbsolutePath() : baseDirPath;

        // 1) Normalize raw "...jar!/entry" into jar: URI if needed
        String normalized = resource.trim();
        int bangPos = indexOfBang(normalized);
        if (bangPos >= 0)
        {
            URI jarUri = buildJarUriFromBang(normalized, bangPos, baseDir);
            return ResourceHandle.forJarUri(jarUri);
        }

        if (!asResource)
        {
            // 2) If it parses as an absolute URI, handle by scheme
            URI candidateUri = tryParseUri(normalized);
            if (candidateUri != null && candidateUri.isAbsolute())
            {
                String scheme = candidateUri.getScheme();
                if ("file".equalsIgnoreCase(scheme))
                {
                    Path p = Path.of(candidateUri);
                    return ResourceHandle.forFile(p);
                }
                if ("jar".equalsIgnoreCase(scheme))
                {
                    return ResourceHandle.forJarUri(candidateUri);
                }
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                {
                    if (httpClient == null)
                    {
                        httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
                    }
                    return ResourceHandle.forHttp(candidateUri, httpClient);
                }
                if ("ftp".equalsIgnoreCase(scheme))
                {
                    return ResourceHandle.forGenericUrl(uriToUrl(candidateUri));
                }
                // unknown but absolute — try URL stream
                return ResourceHandle.forGenericUrl(uriToUrl(candidateUri));
            }

            // 3) Try as a local/relative path
            Path p = baseDir.resolve(resource).normalize();
            if (Files.exists(p))
            {
                return ResourceHandle.forFile(p);
            }
        }

        // 4) Try classpath
        String cpName = resource.startsWith("/") ? resource.substring(1) : resource;
        URL url = cl.getResource(cpName);
        if (url == null)
        {
            url = cl.getResource("resources/" + cpName);
        }
        if (url != null)
        {
            // Could be "jar:file:...!/entry" or plain "file:"
            if ("jar".equalsIgnoreCase(url.getProtocol()) || url.toString().contains("!/"))
            {
                URI jarUri = ensureJarUri(url);
                return ResourceHandle.forJarUri(jarUri).markClassPath();
            }
            else if ("file".equalsIgnoreCase(url.getProtocol()))
            {
                try
                {
                    return ResourceHandle.forFile(Path.of(url.toURI())).markClassPath();
                }
                catch (URISyntaxException e)
                {
                    // fallback to generic
                    return ResourceHandle.forGenericUrl(url).markClassPath();
                }
            }
            else
            {
                return ResourceHandle.forGenericUrl(url).markClassPath();
            }
        }
        throw new NoSuchElementException("Resource not found: " + resource);
    }

    /**
     * Resolve a resource string against a base directory and classloader.
     * @param resource the specification of the resource to load
     * @param classLoader the class loader to specifically use (can be null)
     * @param baseDirPath the potential base directory to which the resource is relative (can be null)
     * @return a resource handle to the resource
     * @throws NoSuchElementException when the resource could not be found
     */
    public static ResourceHandle resolve(final String resource, final ClassLoader classLoader, final Path baseDirPath)
    {
        return resolve(resource, classLoader, baseDirPath, false);
    }

    /**
     * Resolve a resource string against a base directory and classloader.
     * @param resource the specification of the resource to load
     * @param classLoader the class loader to specifically use (can be null)
     * @param baseDir the potential base directory to which the resource is relative
     * @return a resource handle to the resource
     * @throws NoSuchElementException when the resource could not be found
     * @throws InvalidPathException if the baseDir path string cannot be converted to a Path
     */
    public static ResourceHandle resolve(final String resource, final ClassLoader classLoader, final String baseDir)
    {
        Path baseDirPath = Path.of(baseDir);
        return resolve(resource, classLoader, baseDirPath, false);
    }

    /**
     * resolve() method without ClassLoader and base path.
     * @param resource the specification of the resource to resolve
     * @param baseDirPath the potential base directory to which the resource is relative (can be null)
     * @return a resource handle to the resource
     * @throws NoSuchElementException when the resource could not be found
     */
    public static ResourceHandle resolve(final String resource, final Path baseDirPath)
    {
        return resolve(resource, null, baseDirPath, false);
    }

    /**
     * resolve() method without ClassLoader and base path.
     * @param resource the specification of the resource to resolve
     * @param baseDir the potential base directory to which the resource is relative
     * @return a resource handle to the resource
     * @throws NoSuchElementException when the resource could not be found
     * @throws InvalidPathException if the baseDir path string cannot be converted to a Path
     */
    public static ResourceHandle resolve(final String resource, final String baseDir)
    {
        Path baseDirPath = Path.of(baseDir);
        return resolve(resource, null, baseDirPath, false);
    }

    /**
     * resolve() method without ClassLoader.
     * @param resource the specification of the resource to resolve
     * @return a resource handle to the resource
     * @throws NoSuchElementException when the resource could not be found
     */
    public static ResourceHandle resolve(final String resource)
    {
        return resolve(resource, null, null, false);
    }

    /**
     * Resolve the resource relative to the classloader path.
     * @param resource the specification of the resource to resolve
     * @return a resource handle to the resource
     * @throws NoSuchElementException when the resource could not be found
     */
    public static ResourceHandle resolveAsResource(final String resource)
    {
        return resolve(resource, null, null, true);
    }

    // --- helpers ---

    /**
     * Find the 'bang' sign (!/ or !\) inside a resource string.
     * @param s the resource string that might contain "!/" or "!\"
     * @return the location of the bang sign
     */
    private static int indexOfBang(final String s)
    {
        // Normalize Windows "!\" to "!/"
        int i = s.indexOf("!/");
        if (i >= 0)
        {
            return i;
        }
        i = s.indexOf("!\\");
        return i;
    }

    /**
     * Build a valid URI for a jar entry from a bang resource string.
     * @param raw the raw resource string
     * @param bangPos the location of the bang sign "!/" in the resource string
     * @param baseDir the base directory against which we resolve (cannot be null)
     * @return a valid URI for the jar entry
     */
    private static URI buildJarUriFromBang(final String raw, final int bangPos, final Path baseDir)
    {
        // Normalize any "!\" to "!/"
        String s = raw.replace("!\\", "!/");
        int bang = s.indexOf("!/");
        String leftRaw = s.substring(0, bang).trim();
        String entry = s.substring(bang + 2);

        // If 'left' is already an absolute URI (file:, http:, https:, etc.) use it directly
        URI leftAbs = tryParseAbsoluteUri(leftRaw);
        if (leftAbs != null)
        {
            return URI.create("jar:" + leftAbs + "!/" + entry);
        }

        // --- Windows safety: fix "/C:/..." forms BEFORE Path.of(...)
        leftRaw = normalizeWindowsDrive(leftRaw);

        Path jarPath = Path.of(leftRaw);
        if (!jarPath.isAbsolute())
        {
            jarPath = baseDir.resolve(jarPath);
        }
        jarPath = jarPath.normalize();

        URI fileUri = jarPath.toUri(); // properly escaped
        return URI.create("jar:" + fileUri + "!/" + entry);
    }

    /**
     * On Windows, path can get mangled when moving between URI/URL and Path. This method turns "/C:/foo" or "\C:\foo" into
     * "C:/foo" (or "C:\foo")
     * @param s the path string to check
     * @return a normalized string for Windows
     */
    private static String normalizeWindowsDrive(final String s)
    {
        if (File.separatorChar == '\\' && s.length() >= 4 && (s.charAt(0) == '/' || s.charAt(0) == '\\')
                && Character.isLetter(s.charAt(1)) && s.charAt(2) == ':' && (s.charAt(3) == '/' || s.charAt(3) == '\\'))
        {
            return s.substring(1);
        }
        return s;
    }

    /**
     * Try to parse an absolute URI from a resource string (e.g., file:// or http://).
     * @param s the resource string to parse
     * @return a URI for the absolute path to the resource, or null when it could not be found
     */
    private static URI tryParseAbsoluteUri(final String s)
    {
        try
        {
            URI u = URI.create(s);
            return u.isAbsolute() ? u : null;
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    /**
     * Try to create a URI from the given String.
     * @param s the specification that might be convertible into a URI
     * @return a URI or null when s cannot be converted to a URI
     */
    private static URI tryParseUri(final String s)
    {
        try
        {
            return URI.create(s);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    /**
     * Try to convert the given URI into a URL.
     * @param u the URI that might be convertible into a URL
     * @return a URL or null when u cannot be converted to a URL
     */
    private static URL uriToUrl(final URI u)
    {
        try
        {
            return u.toURL();
        }
        catch (MalformedURLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Create a URI for a jar-entry from a URL.
     * @param url the url for the path that might start with "jar:"; if not it is added
     * @return a URI for the jar entry
     */
    private static URI ensureJarUri(final URL url)
    {
        String s = url.toString();
        if (!s.startsWith("jar:"))
        {
            s = "jar:" + s;
        }
        return URI.create(s);
    }

    // ------------------------------------------------------------------------

    /**
     * Inner class with the specifications for a resource.
     */
    public static final class ResourceHandle
    {
        /** The URI of the resource. */
        private final URI uri;

        /** The kind of resource: file, JAR entry, HTTP resource, or generic URL. */
        private final Kind kind;

        /** Optional http-client to help retrieve the resource, can be null. */
        private final HttpClient httpClient;

        /** Relative to classpath? */
        private boolean fromClasspath;

        /**
         * Instantiate a ResourceHandle with the specifications for a resource.
         * @param uri the URI of the resource
         * @param kind the kind of resource: file, JAR entry, HTTP resource, or generic URL
         * @param httpClient the http-client to help retrieve the resource, can be null for non-HTTP resources
         */
        private ResourceHandle(final URI uri, final Kind kind, final HttpClient httpClient)
        {
            this.uri = uri;
            this.kind = kind;
            this.httpClient = httpClient;
        }

        /**
         * Return the URI of the resource.
         * @return the URI of the resource
         */
        public URI asUri()
        {
            return this.uri;
        }

        /**
         * Return the URL of the resource, if it can be transformed to a URL. If not, an Exception is thrown.
         * @return the URI of the resource, if it can be transformed to a URL
         * @throws IllegalStateException when the URI cannot be transformed into a URL
         */
        public URL asUrl()
        {
            try
            {
                return this.uri.toURL();
            }
            catch (MalformedURLException e)
            {
                throw new IllegalStateException(e);
            }
        }

        /**
         * Return a readable stream of the resource for supported schemes.
         * @return a readable stream of the resource for supported schemes
         * @throws IOException when an I/O error occurs during building of the stream
         */
        public InputStream openStream() throws IOException
        {
            switch (this.kind)
            {
                case FILE ->
                {
                    return Files.newInputStream(Path.of(this.uri));
                }
                case JAR_ENTRY ->
                {
                    JarCache.JarEntryPath entry = JarCache.resolveJarEntry(this.uri);
                    return Files.newInputStream(entry.path());
                }
                case HTTP ->
                {
                    HttpRequest.Builder b = HttpRequest.newBuilder(this.uri).GET();
                    // Optional: add Basic auth if userInfo is present (discouraged, but supported)
                    String userInfo = this.uri.getUserInfo();
                    if (userInfo != null && !userInfo.isEmpty())
                    {
                        String enc =
                                Base64.getEncoder().encodeToString(userInfo.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                        b.header("Authorization", "Basic " + enc);
                    }
                    try
                    {
                        HttpResponse<InputStream> resp =
                                this.httpClient.send(b.build(), HttpResponse.BodyHandlers.ofInputStream());
                        if (resp.statusCode() >= 200 && resp.statusCode() < 300)
                        {
                            return resp.body();
                        }
                        resp.body().close();
                        throw new IOException("HTTP " + resp.statusCode() + " for " + this.uri);
                    }
                    catch (InterruptedException ie)
                    {
                        throw new IOException("Interrupted while loading HTTP resource", ie);
                    }
                }
                case GENERIC_URL ->
                {
                    return asUrl().openStream();
                }
                default -> throw new UnsupportedOperationException("Unknown kind: " + this.kind);
            }
        }

        /**
         * A Path is available for plain files and for JAR entries (mounted file system).
         * @return a Path representation of the resource or null when no Path exists.
         */
        public Path asPath()
        {
            try
            {
                return switch (this.kind)
                {
                    case FILE -> Path.of(this.uri);
                    case JAR_ENTRY -> JarCache.resolveJarEntry(this.uri).path();
                    default -> null;
                };
            }
            catch (Exception e)
            {
                return null;
            }
        }

        /**
         * Return whether the resource was loaded from the classpath.
         * @return whether the resource was loaded from the classpath or not
         */
        public boolean isClassPath()
        {
            return this.fromClasspath;
        }

        /**
         * Mark that the resource was loaded from the classpath.
         * @return the ResourceHandle for fluent design
         */
        private ResourceHandle markClassPath()
        {
            this.fromClasspath = true;
            return this;
        }

        /**
         * Instantiate a ResourceHandle from a given Path.
         * @param p the Path
         * @return a ResourceHandle for the Path
         */
        static ResourceHandle forFile(final Path p)
        {
            return new ResourceHandle(p.toUri(), Kind.FILE, null);
        }

        /**
         * Instantiate a ResourceHandle from a jar-entry containing URI such as <code>jar:file:/.../lib.jar!/path/inside</code>.
         * @param jarUri the jar-entry containing URI
         * @return a ResourceHandle for the jar-entry containing URI
         */
        static ResourceHandle forJarUri(final URI jarUri)
        {
            return new ResourceHandle(jarUri, Kind.JAR_ENTRY, null);
        }

        /**
         * Instantiate a ResourceHandle for a given URI for a http-connection using the provided http-client.
         * @param httpUri a URI for a http-connection
         * @param http the http-client to use
         * @return a ResourceHandle for the given URI for a http-connection
         */
        static ResourceHandle forHttp(final URI httpUri, final HttpClient http)
        {
            return new ResourceHandle(httpUri, Kind.HTTP, http);
        }

        /**
         * Instantiate a ResourceHandle from a given generic URL. Throw an exception when the resource handle cannot be
         * instantiated from the URL.
         * @param url the generic URL
         * @return a ResourceHandle for the given URL
         * @throws IllegalArgumentException when the resource handle cannot be instantiated from the URL
         */
        static ResourceHandle forGenericUrl(final URL url)
        {
            try
            {
                return new ResourceHandle(url.toURI(), Kind.GENERIC_URL, null);
            }
            catch (URISyntaxException e)
            {
                throw new IllegalArgumentException(e);
            }
        }

        /**
         * Enum for the kind of resource handle: file, JAR entry, HTTP resource, or generic URL.
         */
        enum Kind
        {
            /** File via the local file system, but not a JAR entry. */
            FILE,

            /** Entry within a JAR file, typically indicated with a "!/" inside the string. */
            JAR_ENTRY,

            /** File on the Internet that can be retrieved via the http(s) protocol. */
            HTTP,

            /** Any other type of URL that might point to the resource. */
            GENERIC_URL
        }
    }

    /**
     * JAR FileSystem caching for FileSystems that need to be mounted when retrieving a Jar.
     */
    private static final class JarCache
    {
        /** Cache JAR root URI -> FileSystem (weak so closed/reclaimed when unused). */
        private static final Map<URI, FileSystem> FS_CACHE = new ConcurrentHashMap<>();

        /**
         * Record to return the combination of a FileSystem and a Path within that FileSystem.
         * @param fs the FileSystem
         * @param path the Path within the FileSystem
         */
        record JarEntryPath(FileSystem fs, Path path)
        {
        }

        /**
         * Resolve a Jar entry, such as jar:file:/.../lib.jar!/path/in/jar.
         * @param jarUri the URI that stores the JAR path
         * @return a FileSystem plus Path record for the JAR entry
         * @throws IllegalArgumentException when the URI does not contain a JAR entry
         */
        static JarEntryPath resolveJarEntry(final URI jarUri)
        {
            String ssp = jarUri.getSchemeSpecificPart();
            int bang = ssp.indexOf("!/");
            if (bang < 0)
            {
                throw new IllegalArgumentException("Not a JAR URI: " + jarUri);
            }
            URI jarFileUri = URI.create(ssp.substring(0, bang)); // file:/.../lib.jar
            String entry = ssp.substring(bang + 2); // path/in/jar

            FileSystem fs = FS_CACHE.computeIfAbsent(jarFileUri, k ->
            {
                try
                {
                    // Reuse if already mounted; else mount
                    try
                    {
                        return FileSystems.getFileSystem(URI.create("jar:" + k));
                    }
                    catch (FileSystemNotFoundException ignored)
                    {
                        return FileSystems.newFileSystem(URI.create("jar:" + k), Map.of());
                    }
                }
                catch (IOException e)
                {
                    throw new UncheckedIOException(e);
                }
            });

            return new JarEntryPath(fs, fs.getPath(entry));
        }
    }
}
