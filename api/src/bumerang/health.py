from tornado.web import RequestHandler


class HealthCheckHandler(RequestHandler):

    def get(self):
        """A basic health check to see whether the API can be reached or not.

        :rtype: dict
        :return: A dictionary with one key, live, which is true if the API can
                 be reached and is running
        """
        response = {'live': True}
        self.write(response)
