import {authInstance, instance} from "@/api/instance";

// 매거진 등록
const createMagazineAPI = function (formData) {
    return authInstance.post(`/api/magazines`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

// 매거진 수정
const updateMagazineAPI = function (id, formData) {
    return authInstance.patch(`/api/magazines/${id}`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

// 매거진 삭제
const deleteMagazineAPI = function (id) {
    return authInstance.delete(`/api/magazines/${id}`);
};

// 매거진 단일 조회
const getMagazineAPI = function (id) {
    return instance.get(`/api/magazines/${id}`);
}

// 매거진 목록 조회
const getMagazineListAPI = function (request) {
    return instance.get(
        `/api/magazines`, {
            params: {
                keyword: request.keyword,
                size: request.size,
                page: request.page
            }
        });
}

export {
    createMagazineAPI,
    updateMagazineAPI,
    deleteMagazineAPI,
    getMagazineAPI,
    getMagazineListAPI
}
