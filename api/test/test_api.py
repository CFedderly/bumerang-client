import pytest as _pytest
import requests


@_pytest.fixture
def host():
    """The host that the tests will run against"""
    return 'http://localhost:8888/'

def test_health(host):
    """Test the health of our API"""
    slug = 'health'
    response = requests.get(host+slug)
    assert response.ok, 'Invalid response from server'

    json_response = response.json()
    assert json_response.get('live', False), 'The API is not live'
