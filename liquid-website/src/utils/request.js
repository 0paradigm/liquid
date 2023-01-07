import axios from 'axios'
import {getToken} from '@/utils/auth'
import {Message} from '@arco-design/web-vue'
import router from "@/router";


const instance = axios.create({
    timeout: 5000
})

instance.interceptors.request.use(
    config => {
        const token = getToken();
        if (token) {
            config.headers.Token = token
            config.headers.Authorization = token
        }
        return config
    },

    error => Promise.reject(error)
)

instance.interceptors.response.use(
    response => {
        return response
    },
    error => {
        // Message.error(error.response.data.msg)
        if (error.response.status === 404) {
            router.push({name: 'notFound', params: {pathMatch: router.currentRoute.value.path.substring(1).split('/')}})
        }
        return Promise.reject(error)
    }
)

export default instance;
