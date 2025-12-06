// src/api/holidayApi.js
import apiClient from './client'

// 공휴일 검색 (연도/상세검색 공용)
export async function searchHolidays({
  mode,
  year,
  countryCode,
  from,
  to,
  type,
  localNameKeyword,
  englishNameKeyword,
  fixedHoliday,
  globalHoliday,
  launchYearFrom,
  launchYearTo,
  page = 0,
  size = 20,
}) {
  const params = { page, size }

  if (year) params.year = year
  if (countryCode) params.countryCode = countryCode
  if (from) params.from = from
  if (to) params.to = to
  if (type) params.type = type
  if (localNameKeyword) params.localNameKeyword = localNameKeyword
  if (englishNameKeyword) params.englishNameKeyword = englishNameKeyword
  if (fixedHoliday !== undefined && fixedHoliday !== null) {
    params.fixedHoliday = fixedHoliday
  }
  if (globalHoliday !== undefined && globalHoliday !== null) {
    params.globalHoliday = globalHoliday
  }
  if (launchYearFrom) params.launchYearFrom = launchYearFrom
  if (launchYearTo) params.launchYearTo = launchYearTo

  const res = await apiClient.get('/holidays', { params })
  return res.data // ResponseDto<Page<HolidayResponseDto>>
}

// 특정 연도 + 국가 재동기화 (기존 데이터 삭제 후 다시 적재)
export async function refreshHolidays({ year, countryCode }) {
  const res = await apiClient.put('/holidays/refresh', null, {
    params: { year, countryCode },
  })
  return res.data // ResponseDto<Void>
}

// 특정 연도 + 국가 삭제
export async function deleteHolidays({ year, countryCode }) {
  const res = await apiClient.delete('/holidays', {
    params: { year, countryCode },
  })
  return res.data // ResponseDto<Void>
}
