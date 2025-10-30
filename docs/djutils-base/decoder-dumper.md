# DecoderDumper

Quite often, a programmer needs to express bytes, or more structured data in a textual format for inspection, or debugging. The DecoderDumper package is a framework for doing just that. The package contains a couple of dumpers of which the `HexDumper` is the most used. With the `HexDumper` a hexadecimal dump of an array of bytes is created with a minimum of code like:

```java
byte[] bytes = new byte[] { 10, 20, 0x04, (byte) 0xff, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' };
System.out.println(HexDumper.hexDumper(bytes));
```

This outputs:

```text
00000000: 0a 14 04 ff 61 62 63 64  65 66 67 68 69 6a 6b 6c  ....abcd efghijkl
00000010: 6d                                                m                
```

By default, the address of the first byte is 0. This can be overridden by explicitly setting the start address like:

```java
System.out.println(HexDumper.hexDumper(123, bytes));
```

Which outputs:

```text
00000070:                                   0a 14 04 ff 61              ....a
00000080: 62 63 64 65 66 67 68 69  6a 6b 6c 6d              bcdefghi jklm    
```

No lines are output for addresses below the initial offset of 123. The first output line starts with address 00000070 (112 decimal). Then 11 placeholders are output for bytes 112 up to 122 and the first byte of `bytes` is output in the 12th position of the line with address 00000070.

If the output needs to be sent to some stream, a Dumper must be explicitly instantiated and then manipulated to change the output device. Output is buffered until a line is complete, or until the user explicitly forces all buffered data to be output.

```java
byte[] bytes = new byte[] { 10, 20, 0x04, (byte) 0xff, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' };
HexDumper hexDumper = new HexDumper();
hexDumper.setOutputStream(System.err); // from now on, all output is sent to the error output
hexDumper.append(bytes); // will output the first complete line; buffer byte 17 as first byte of the next output line
System.err.println("before flush");
hexDumper.flush(); // force buffered output to printed
System.err.println("adding more data");
byte[] moreBytes = new byte[] { -1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12, -13, -14, -15, -16, -17 };
hexDumper.append(moreBytes); // will output complete line with gap for previously output data and buffer two bytes
System.err.println("before 2nd flush");
hexDumper.flush(); // force the two buffered bytes to be output
```

This will output seven lines (on the System.err OutputStream):
```text
00000000: 0a 14 04 ff 61 62 63 64  65 66 67 68 69 6a 6b 6c  ....abcd efghijkl
before flush
00000010: 6d                                                m                
adding more data
00000010:    ff fe fd fc fb fa f9  f8 f7 f6 f5 f4 f3 f2 f1   ....... ........
before 2nd flush
00000020: f0 ef                                             ..               
```

Dumpers can easily be created for any output format and any data type. Each Dumper has a list of decoders. At run time, input data (the bytes that are to be decoded) is fed, one byte at a time, to all those decoders in sequence. Each decoder is responsible for constructing its own part of an output line. When any of the decoders signals that an output line must be emitted, the Dumper will collect the constructed output from each of the decoders, concatenate it and output the result (unless suppression of multiple output lines with identical data is on and this output line matches the preceding output line). Six decoders are used in the `HexDumper` in this order:
* HexAddressDecoder(16)
* FixedString(": ")
* HexDecoder(16, 8)
* FixedString("  ")
* CharDecoder(16, 8)
* FixedString("\n");

The simplest one of these is the `FixedString` decoder. This decoder outputs a fixed part into output line. Three `FixedString` decoders are used in the HexDumper. The `HexAddressDecoder` is only slightly more complex. it prints the 8 hexadecimal digit address at the start of each output line. The `HexDecoder` expresses a byte as two hexadecimal digits plus a trailing space. The `CharDecoder` outputs a printable byte as the corresponding character, all other bytes as a dot (`.`). The `HexDecoder` and `CharDecoder` take two arguments in their constructors. The first specifies the maximum number of bytes that can be output in one line of output. The second argument specifies that an extra space is to be inserted every n positions.

Each decoder implements the `Decoder` interface. This interface specifies that a `Decoder` shall implement four methods:

```java
    String getResult();
    int getMaximumWidth();
    boolean append(int address, byte theByte) throws IOException;
    boolean ignoreForIdenticalOutputCheck();
```

The `getResult` method returns a String that is either zero length (if no data was appended), or the length of the maximum output width of the decoder (as reported by the next method). If some bytes have been appended, but the the result under construction is not yet completely filled, the positions reserved for the missing bytes must be filled with spaces. After a call to `getResult` the internal buffer is reset to zero length.

The `getMaximumWidth` method returns the width of an output line.

The `append` method processes one byte. Arguments of this methods are `address` (the accumulated address which is equal to the initial offset plus the number of bytes processed before this call) and `theByte` (the byte that must be appended). This method must return true if the accumulated output should be flushed (for most decoders this happens when the last position of the output line under construction has been filled).

The `ignoreForIdentialOutputCheck` method indicates whether the output of this `Decoder` is to be used in the test that suppressed output lines with the same content. Decoders that output `theByte` in some form should report `true`; other decoders (that have fixed output, or output that only depends on the `address` parameter of their `append` method) should report `false`.

Suppression of multiple lines with the same data is implemented in the `Dumper` class. It is on by default, but can be switched off (and on again) by calling the `setSuppressMultipleIdenticalLines` method.


## Other dumpers

Besides the `HexDumper` a `Base64Dumper` is provided. As the name suggest, this dumper takes bytes that expected to be [base64](https://en.wikipedia.org/wiki/Base64) encoded data and converts that into a human readable form. Some bytes are invalid in base64 encoded data. If one such byte is encountered, the decoder emits a complaint using the `CategoryLogger` but otherwise continues to attempt to decode the provided data. Base64 encoded data ends with a byte with value 61 (0x3D, `=`). After encountering such a byte, no further data is decoded (i.e., the `Base64Decoder` does not handle multiple base64 encoded objects in its input).

The `djutils-serialization` project contains the `SerialDataDumper` that can decode the serialized data into a somewhat human readable form. As the objects in the input of that Dumper may differ wildely in size, this dumper has some extra complexitiy that outputs larger objects using multiple output lines while ensuring that all those output lines fit within a reasonable output line size.
