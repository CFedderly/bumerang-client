.PHONY: python_image image server test clean
server: image
	docker run -d -p 127.0.0.1:8888:8888 --name bumerang_instance \
	bumerang_api python3 src/bumerang/app.py
test: image
	docker run --name bumerang_test_instance bumerang_api \
    /bin/sh -c ./run_tests.sh
image:
	docker build -t bumerang_api .
python_image:
	docker build -t python_bumerang python/
