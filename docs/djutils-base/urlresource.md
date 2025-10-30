# URLResource class

The URLResource class helps to quickly find the location of a resource under different circumstances. Included retrieval possibilities are:

* File on the file system
* File on the current classpath
* File on a network drive
* File accessible on a network drive through a username and password
* File relative to a provided location
* InputStream based on the file for any of the above situations

In order to retrieve a file for the above situations, URLResource tries different approaches with `try ... catch` to see if it can find the file. The sequence is as follows:

* If the path starts with a '/', `URLResource.class.getResource(name)` is tested first. This uses the URLResource object's class loader to find the file with the given name. If URLResource was loaded by the bootstrap class loader, the method delegates to the ClassLoader.getSystemResource(name) method. If it retrieves a valid file, the corresponding URL is returned.
* If the path starts with a '/', `Thread.currentThread().getContextClassLoader().getResource(name_without_initial_slash)` is tested second. This uses the class loader of the thread to find the file with the given name. If it retrieves a valid file, the corresponding URL is returned.
* If the path starts with a '/', `new File(name).exists()` is called. If the file exists, the corresponding URL based on the filename, preceded by "file:" is returned.
* If the path starts with '\\', the corresponding URL based on the filename, preceded by "file:" is returned (which can be null).
* If `new File(name).exists()` returns true, the corresponding URL based on the filename, preceded by "file:" is returned.
* If the name contains a '@' sign, authorization is attempted. An example is file://username:password@10.0.0.1/D$/Temp/file.txt 

The `getResource(filename, base)` method uses the above methods to find the file relative to the given base directory or location.

The `getResourceAsStream(name)` method uses the above methods to find the file, and return it as an InputStream to read from.
