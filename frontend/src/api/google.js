import {authInstance} from "@/api/instance";

const searchPlacesAPI = function (request) {
    return authInstance.get(
        `/api/google-maps/search`, {
            params: {
                textQuery: request.textQuery,
                pageSize: request.pageSize,
                pageToken: request.pageToken,
                latitude: request.latitude,
                longitude: request.longitude
            }
        }
    )
}

const detailPlaceAPI = function (id) {
    return authInstance.get(
        `/api/google-maps/detail`, {
            params: {
                id: id
            }
        }
    )
};

const mapLoadAPI = function () {
    return authInstance.get(`/api/google-maps/load`)
};

export {
    searchPlacesAPI,
    detailPlaceAPI,
    mapLoadAPI
}
