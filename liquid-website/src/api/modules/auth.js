import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";

export default {
    login:{
        register(login, mail, password, phone){
            return request.post(BACKEND_URL + '/core/api/user/register', {
                login: login,
                mail: mail,
                password: password,
                phone: phone
            })
        }
    }
}