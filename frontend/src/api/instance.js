import axios from "axios";
import {setAuthInterceptors, setInterceptors} from "@/api/interceptor";

const createInstance = function () {
    const instance = axios.create();
    return setInterceptors(instance);
}

export const instance = createInstance();

const createAuthInstance = function () {
    const instance = axios.create();
    return setAuthInterceptors(instance);
};

export const authInstance = createAuthInstance();
