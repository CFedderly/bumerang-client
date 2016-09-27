from setuptools import (
    setup,
    find_packages
)

setup(
    name='bumerang',
    version='0.1',
    description='',
    install_requires=['tornado'],
    setup_requires=['pytest-runner'],
    tests_require=[
        'mock',
        'pytest'
    ],
    test_suite='test',
    package_dir={'': 'src'},
    packages=find_packages('src'),
)
