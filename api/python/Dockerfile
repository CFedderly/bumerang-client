FROM centos

# Update centos and grab the tools to install python
RUN yum update -y && \
	yum install wget openssl-devel openssl -y && \
	yum groupinstall "Development Tools" -y

# Install Python
RUN wget https://www.python.org/ftp/python/3.5.2/Python-3.5.2.tgz && \
	tar xzf Python-3.5.2.tgz && \
	cd Python-3.5.2 && \
	./configure --prefix=/usr/local && \
	make install

# Add our packages for the application
RUN pip3 install --upgrade pip && \
	pip3 install tornado && \
    pip3 install requests
