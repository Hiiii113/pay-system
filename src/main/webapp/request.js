// import axios from 'axios';

// 创建axios实例
const instance = axios.create({
    baseURL: 'http://localhost:8081/pay_system_war_exploded',
    timeout: 10000,
})

// 请求拦截器
instance.interceptors.request.use(
    // 成功则返回让axios继续请求
    (config) => {
        return config
    },
    // 失败则抛出异常error
    (error) => Promise.reject(error)
)

// 响应拦截器
instance.interceptors.response.use(
    // 成功接收则返回数据
    (response) => {
        return response.data
    },
    // 失败则抛出异常
    (error) => Promise.reject(error)
)

// 封装get和post请求
window.get = function (url, params) {
    // url: 路径地址  params: 参数
    return instance.get(url, {params})
}

window.post = function (url, data) {
    // url: 路径地址  data: 数据
    return instance.post(url, data)
}