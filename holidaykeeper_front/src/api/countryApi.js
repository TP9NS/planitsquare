import apiClient from './client'

// 전체 국가 목록
export async function fetchAllCountries() {
  const res = await apiClient.get('/countries')
  return res.data.data
}

// 이름 검색 (부분 일치)
export async function searchCountries(keyword) {
  const res = await apiClient.get('/countries/search', {
    params: { keyword },
  })
  return res.data.data
}

// 단일 국가 조회
export async function fetchCountry(countryCode) {
  const res = await apiClient.get(`/countries/${countryCode}`)
  return res.data.data
}
