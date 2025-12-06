import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

console.log('API BASE URL =', baseURL)

const apiClient = axios.create({
  baseURL,
  timeout: 5000,
})

// 응답 인터셉터
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response || error.message)
    return Promise.reject(error)
  }
)

export default apiClient