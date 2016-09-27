# bümerang API

## Running the API locally
* To create an instance of the server, run: `make server`
* To run the unit tests for the server, run: `make test` 
* Note: For either of these targets to work, the `image` is required.
Also after killing the server, the container must be cleaned up in order
to run another instance. This can be done with
`docker rm bumerang_instance`. Similarly, after each test run,
`docker rm bumerang_test_instance` is needed. This will be automated in the future.

### Building the image
Two images are currently available to build for project, `python_image` and
`image`. The Python image is an image that contains the development tools
and packages, associated with Python 3.5 on top of Centos7. The `image`
involved the bümerang source installed in addition to the `python_image`.

* To build `python_image`, run: `make python_image`
* To build `image`, run: `make image`