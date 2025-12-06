import { useEffect, useState } from 'react'
import { fetchAllCountries, searchCountries } from '../api/countryApi'

function CountrySearch({ onSelectCountry }) {
  const [keyword, setKeyword] = useState('')
  const [countries, setCountries] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await fetchAllCountries()
        setCountries(data)
      } catch (e) {
        console.error(e)
        setError('국가 목록을 불러오는데 실패했습니다.')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const handleSearch = async (e) => {
    e.preventDefault()
    try {
      setLoading(true)
      setError(null)

      if (!keyword.trim()) {
        const data = await fetchAllCountries()
        setCountries(data)
        return
      }

      const data = await searchCountries(keyword.trim())
      setCountries(data)
    } catch (e) {
      console.error(e)
      setError('국가 검색 중 오류가 발생했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div
      style={{
        border: '1px solid #ddd',
        borderRadius: 8,
        padding: 16,
        boxSizing: 'border-box',
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      <h2 style={{ marginBottom: 12 }}>국가 검색</h2>

      <form
        onSubmit={handleSearch}
        style={{ display: 'flex', gap: 8, marginBottom: 8 }}
      >
        <input
          type="text"
          placeholder="국가 이름 검색 (예: Korea, United)"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          style={{ flex: 1, padding: 8 }}
        />
        <button type="submit" style={{ padding: '8px 16px' }}>
          검색
        </button>
      </form>

      {loading && <p>로딩 중...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <ul
        style={{
          flex: 1,
          overflowY: 'auto',
          paddingLeft: 0,
          listStyle: 'none',
          marginTop: 8,
        }}
      >
        {countries.map((c) => (
          <li
            key={c.countryCode}
            style={{
              padding: '6px 8px',
              cursor: 'pointer',
              borderRadius: 4,
              border: '1px solid #eee',
              marginBottom: 4,
            }}
            onClick={() => onSelectCountry?.(c)}
          >
            <strong>{c.countryCode}</strong> - {c.name}
          </li>
        ))}
        {countries.length === 0 && !loading && (
          <li>표시할 국가가 없습니다.</li>
        )}
      </ul>
    </div>
  )
}

export default CountrySearch
