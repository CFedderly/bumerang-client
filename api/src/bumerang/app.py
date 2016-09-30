from bumerang.health import HealthCheckHandler

from tornado.ioloop import IOLoop
from tornado.options import define
from tornado.options import options
from tornado.web import Application


define('debug', default=True, help='debug is on or not')
define('port', default=8888, help='run on given port', type=int)


def main():
    app = Application(
        [
            (r'/health', HealthCheckHandler),
        ],
        debug=options.debug,
    )
    app.listen(options.port)
    IOLoop.current().start()


if __name__ == '__main__':
    main()
