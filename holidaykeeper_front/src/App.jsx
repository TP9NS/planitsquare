import { useState } from 'react'
import CountrySearch from './components/CountrySearch'
import HolidaySearch from './components/HolidaySearch'

function App() {
  const [selectedCountry, setSelectedCountry] = useState(null)

  return (
    <div style={{ minHeight: '100vh' ,width: '100vw',maxWidth: '100vw', backgroundColor: '#f5f5f5' }}>
      <header
        style={{
          padding: '16px 24px',
          backgroundColor: '#1e88e5',
          color: 'white',
          marginBottom: 16,
        }}
      >
        <h1 style={{ margin: 0, fontSize: 24 }}>HolidayKeeper</h1>
        <p style={{ margin: 0, marginTop: 4, fontSize: 14 }}>국가별 공휴일 검색</p>
      </header>

      <main
        style={{
          padding: '0 24px 24px',
          boxSizing: 'border-box',
          height: 'calc(100vh - 80px)',
        }}
      >
        <div
          style={{
            display: 'flex',
            gap: 16,
            alignItems: 'stretch',
            height: '100%',
          }}
        >
          <div style={{ width: 320, height: '100%' }}>
            <CountrySearch onSelectCountry={setSelectedCountry} />
          </div>

          <div style={{ flex: 1, minWidth: 0, height: '100%' }}>
            <HolidaySearch selectedCountry={selectedCountry} />
          </div>
        </div>
      </main>
    </div>
  )
}

export default App
