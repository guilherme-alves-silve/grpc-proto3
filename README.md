# Study about protocol buffers and gRPC:

* [X] [Complete Guide to Protocol Buffers 3 [Java, Golang, Python]](https://
www.udemy.com/course/protocol-buffers/)
* [ ] [gRPC na pràtica](https://www.youtube.com/playlist?list=PLJZ5NZd1v4dCj-n2QDkGxXwWv7rNmnC5g)
* [ ] [gRPC [Java] Master Class: Build Modern API & Micro services](https://www.udemy.com/course/grpc-java/)

## Code generation (protobuf)

* Java:
	``protoc --java_out=. *.proto``
* Python:
	``protoc --python_out=. *.proto``
* C++:
	``protoc --cpp_out=. *.proto``

## Utilities (protobuf)

![Example --decode_raw](example_decode_raw.jpg)

![Example --decode](example_decode.jpg)

![Example --decode with package](example_decode_pkg.jpg)

![Example --encode](example_encode.jpg)
