import Cookies from 'js-cookie'

const TOKEN_KEY = 'Authorization';

const isLogin = () => {
    return !!Cookies.get(TOKEN_KEY);
};

const getToken = () => {
    return Cookies.get(TOKEN_KEY);
};

const setToken = (token) => {
    Cookies.set(TOKEN_KEY, token)
};

const clearToken = () => {
    Cookies.remove(TOKEN_KEY)
};

export { isLogin, getToken, setToken, clearToken };
