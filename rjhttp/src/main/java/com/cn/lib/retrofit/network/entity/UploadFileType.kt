package com.cn.lib.retrofit.network.entity

enum class UploadFileType {
    /**
     * List<MultipartBody.Part>方式上传
    </MultipartBody.Part> */
    PART_FROM,
    /**
     * Map<String></String>, MultipartBody.Part>方式上传
     */
    PART_MAP,
    /**
     * Map<RequestBody>方式上传
    </RequestBody> */
    BODY_MAP,
    /**
     * RequestBody方式上传，该方式不支持普通参数
     */
    BODY
}
