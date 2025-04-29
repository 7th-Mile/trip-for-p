import {authInstance} from "@/api/instance";

const getChanceAPI = function () {
    return authInstance.get(
        `/api/ai/chance`
    )
};

export {
    getChanceAPI
}
