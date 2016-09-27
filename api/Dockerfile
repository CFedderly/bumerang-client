FROM python_bumerang
LABEL version="0.1" \
	  descripion="A containerized bumerang API"

ADD . /home/dev/Bumerang

WORKDIR /home/dev/Bumerang

RUN python3 setup.py install
