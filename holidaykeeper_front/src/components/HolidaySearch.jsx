// src/components/HolidaySearch.jsx
import { useState } from "react";
import {
  searchHolidays,
  refreshHolidays,
  deleteHolidays,
} from "../api/holidayApi";

// 타입 카테고리 상수
const HOLIDAY_TYPES = [
  { value: "", label: "전체" },
  { value: "Public", label: "Public (법정 공휴일)" },
  { value: "Bank", label: "Bank (은행/오피스 휴무)" },
  { value: "School", label: "School (학교 휴무)" },
  { value: "Authorities", label: "Authorities (관공서 휴무)" },
  { value: "Optional", label: "Optional (선택 휴일)" },
  { value: "Observance", label: "Observance (기념일, 유급휴무X)" },
];

const containerStyle = {
  border: "1px solid #ddd",
  borderRadius: 8,
  padding: 16,
  boxSizing: "border-box",
  width: "100%",
  height: "100%",
  display: "flex",
  flexDirection: "column",
};

function HolidaySearch({ selectedCountry }) {
  const thisYear = new Date().getFullYear();

  // 검색 모드: 연도 모드 / 상세검색 모드
  const [mode, setMode] = useState("YEAR"); // 'YEAR' | 'DETAIL'
  const isYearMode = mode === "YEAR";

  // 연도 모드용
  const [year, setYear] = useState(thisYear);

  // 상세검색용 필터들
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [type, setType] = useState("");

  const [localNameKeyword, setLocalNameKeyword] = useState("");
  const [englishNameKeyword, setEnglishNameKeyword] = useState("");
  const [fixedHoliday, setFixedHoliday] = useState("ALL"); // 'ALL' | 'TRUE' | 'FALSE'
  const [globalHoliday, setGlobalHoliday] = useState("ALL"); // 'ALL' | 'TRUE' | 'FALSE'
  const [launchYearFrom, setLaunchYearFrom] = useState("");
  const [launchYearTo, setLaunchYearTo] = useState("");

  // 페이지 크기(한 페이지에 몇 개)
  const [pageSize, setPageSize] = useState(20);

  // 결과 및 상태
  const [holidays, setHolidays] = useState([]);
  const [pageInfo, setPageInfo] = useState({
    page: 0,
    totalPages: 0,
    totalElements: 0,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [infoMessage, setInfoMessage] = useState(""); // init/refresh/delete 성공 메시지 등

  // 검색 파라미터 조립
  const buildSearchParams = (page = 0) => {
    const base = {
      mode,
      page,
      size: pageSize,
    };

    // 선택된 국가가 있으면 countryCode 필터로 사용
    if (selectedCountry) {
      base.countryCode = selectedCountry.countryCode;
    }

    if (isYearMode) {
      return {
        ...base,
        year,
      };
    }

    // DETAIL 모드
    return {
      ...base,
      from: fromDate || undefined,
      to: toDate || undefined,
      type: type || undefined,
      localNameKeyword: localNameKeyword || undefined,
      englishNameKeyword: englishNameKeyword || undefined,
      fixedHoliday:
        fixedHoliday === "ALL"
          ? undefined
          : fixedHoliday === "TRUE"
          ? true
          : false,
      globalHoliday:
        globalHoliday === "ALL"
          ? undefined
          : globalHoliday === "TRUE"
          ? true
          : false,
      launchYearFrom: launchYearFrom || undefined,
      launchYearTo: launchYearTo || undefined,
    };
  };

  const doSearch = async (page = 0) => {
    // YEAR 모드에서는 국가 + 연도 필수
    if (isYearMode) {
      if (!selectedCountry) {
        setError("먼저 왼쪽에서 국가를 선택해주세요.");
        return;
      }
      if (!year) {
        setError("연도를 입력해주세요.");
        return;
      }

      // 🔹 연도가 현재 연도보다 클 수 없음
      if (Number(year) > thisYear) {
        setError(`연도는 현재 연도(${thisYear})보다 클 수 없습니다.`);
        return;
      }
    } else {
      if (fromDate && toDate) {
        const from = new Date(fromDate);
        const to = new Date(toDate);
        if (from > to) {
          setError("시작일(from)은 종료일(to)보다 늦을 수 없습니다.");
          return;
        }
      }
      if (launchYearFrom && isNaN(Number(launchYearFrom))) {
        setError("launchYear (from)은 숫자만 입력해야 합니다.");
        return;
      }
      if (launchYearTo && isNaN(Number(launchYearTo))) {
        setError("launchYear (to)은 숫자만 입력해야 합니다.");
        return;
      }

      const fromLaunch = Number(launchYearFrom);
      const toLaunch = Number(launchYearTo);
      // 둘 다 입력되었을 때만 비교
      if (launchYearFrom && launchYearTo) {
        if (fromLaunch > toLaunch) {
          setError("launchYear 시작값은 종료값보다 클 수 없습니다.");
          return;
        }
      }
      if (launchYearTo && (toLaunch < 1 || toLaunch > thisYear)) {
        setError(`launchYear (to)은 1 이상 ${thisYear} 이하만 가능합니다.`);
        return;
      }
    }

    try {
      setLoading(true);
      setError(null);
      setInfoMessage("");

      const params = buildSearchParams(page);
      const responseDto = await searchHolidays(params);

      const pageData = responseDto.data;
      setHolidays(pageData?.content ?? []);
      setPageInfo({
        page: pageData?.number ?? page,
        totalPages: pageData?.totalPages ?? 0,
        totalElements: pageData?.totalElements ?? 0,
      });
    } catch (e) {
      console.error(e);
      const msg =
        e.response?.data?.message ||
        e.response?.data?.code ||
        "공휴일 검색 중 오류가 발생했습니다.";
      setError(msg);
      setHolidays([]);
      setPageInfo({ page: 0, totalPages: 0, totalElements: 0 });
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    doSearch(0);
  };

  const handlePrev = () => {
    if (pageInfo.page > 0) {
      doSearch(pageInfo.page - 1);
    }
  };

  const handleNext = () => {
    if (pageInfo.page + 1 < pageInfo.totalPages) {
      doSearch(pageInfo.page + 1);
    }
  };

  const handleModeChange = (e) => {
    const value = e.target.value;
    setMode(value);
    setError(null);
    setInfoMessage("");

    // 모드 바꿀 때 필터 초기화
    if (value === "YEAR") {
      setFromDate("");
      setToDate("");
      setType("");
      setLocalNameKeyword("");
      setEnglishNameKeyword("");
      setFixedHoliday("ALL");
      setGlobalHoliday("ALL");
      setLaunchYearFrom("");
      setLaunchYearTo("");
    }
  };

  const requireYearAndCountry = () => {
    if (!selectedCountry) {
      setError("국가를 먼저 선택해주세요.");
      return false;
    }
    if (!year) {
      setError("연도를 입력해주세요.");
      return false;
    }
    return true;
  };

  const canUseYearActions = isYearMode && selectedCountry && year;

  const handleRefresh = async () => {
    if (!canUseYearActions) return;
    if (!requireYearAndCountry()) return;

    try {
      setLoading(true);
      setError(null);
      setInfoMessage("");

      const res = await refreshHolidays({
        year,
        countryCode: selectedCountry.countryCode,
      });

      setInfoMessage(
        res.message ||
          `연도 ${year}, 국가 ${selectedCountry.countryCode} 공휴일을 재동기화했습니다.`
      );

      // 재동기화 후 현재 페이지 다시 조회
      await doSearch(pageInfo.page);
    } catch (e) {
      console.error(e);
      const msg =
        e.response?.data?.message ||
        e.response?.data?.code ||
        "공휴일 재동기화 중 오류가 발생했습니다.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!canUseYearActions) return;
    if (!requireYearAndCountry()) return;

    if (
      !window.confirm(
        `${year}년, ${selectedCountry.countryCode} 공휴일을 정말 삭제할까요?`
      )
    ) {
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setInfoMessage("");

      const res = await deleteHolidays({
        year,
        countryCode: selectedCountry.countryCode,
      });

      setInfoMessage(
        res.message ||
          `연도 ${year}, 국가 ${selectedCountry.countryCode} 공휴일을 삭제했습니다.`
      );

      setHolidays([]);
      setPageInfo({ page: 0, totalPages: 0, totalElements: 0 });
    } catch (e) {
      console.error(e);
      const msg =
        e.response?.data?.message ||
        e.response?.data?.code ||
        "공휴일 삭제 중 오류가 발생했습니다.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={containerStyle}>
      <h2 style={{ marginBottom: 8 }}>공휴일 검색 / 관리</h2>

      {/* 검색 모드 선택 + 페이지 크기 선택 */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: 12,
          gap: 16,
        }}
      >
        <div>
          <label style={{ marginRight: 16 }}>
            <input
              type="radio"
              value="YEAR"
              checked={isYearMode}
              onChange={handleModeChange}
            />
            &nbsp;연도별 검색 (삭제/재동기화 가능)
          </label>
          <label>
            <input
              type="radio"
              value="DETAIL"
              checked={!isYearMode}
              onChange={handleModeChange}
            />
            &nbsp;상세 검색 (기간/타입 등)
          </label>
        </div>

        <div>
          <label style={{ marginRight: 8 }}>페이지 크기</label>
          <select
            value={pageSize}
            onChange={(e) => setPageSize(Number(e.target.value))}
            style={{ padding: 6 }}
          >
            <option value={10}>10개</option>
            <option value={20}>20개</option>
            <option value={50}>50개</option>
            <option value={100}>100개</option>
          </select>
        </div>
      </div>

      {/* 검색 폼 */}
      <form
        onSubmit={handleSubmit}
        style={
          isYearMode
            ? {
                display: "flex",
                flexWrap: "nowrap",
                alignItems: "flex-end",
                marginBottom: 12,
                columnGap: 12,
                width: "100%",
                justifyContent: "space-between",
              }
            : {
                display: "flex",
                flexWrap: "wrap",
                gap: 12,
                alignItems: "flex-end",
                marginBottom: 12,
                width: "100%",
              }
        }
      >
        {/* 선택된 국가 표시 (공통) */}
        <div
          style={{
            minWidth: 260,
            flex: isYearMode ? 1 : "0 0 auto",
          }}
        >
          <label style={{ display: "block", marginBottom: 4 }}>
            선택된 국가
          </label>
          <input
            type="text"
            value={
              selectedCountry
                ? `${selectedCountry.countryCode} - ${selectedCountry.name}`
                : ""
            }
            disabled
            style={{ padding: 8, width: "100%" }}
            placeholder="왼쪽에서 국가를 선택하세요"
          />
        </div>

        {/* YEAR 모드: 연도 필드 */}
        {isYearMode && (
          <div>
            <label style={{ display: "block", marginBottom: 4 }}>연도</label>
            <input
              type="number"
              value={year}
              onChange={(e) => setYear(e.target.value)}
              style={{ padding: 8, width: 100 }}
            />
          </div>
        )}

        {/* DETAIL 모드: 기간/타입/이름/옵션 필터 */}
        {!isYearMode && (
          <>
            <div>
              <label style={{ display: "block", marginBottom: 4 }}>
                시작일 (from)
              </label>
              <input
                type="date"
                value={fromDate}
                onChange={(e) => setFromDate(e.target.value)}
                style={{ padding: 8 }}
              />
            </div>

            <div>
              <label style={{ display: "block", marginBottom: 4 }}>
                종료일 (to)
              </label>
              <input
                type="date"
                value={toDate}
                onChange={(e) => setToDate(e.target.value)}
                style={{ padding: 8 }}
              />
            </div>

            <div>
              <label style={{ display: "block", marginBottom: 4 }}>
                타입(types)
              </label>
              <select
                value={type}
                onChange={(e) => setType(e.target.value)}
                style={{ padding: 8, width: 220 }}
              >
                {HOLIDAY_TYPES.map((t) => (
                  <option key={t.value} value={t.value}>
                    {t.label}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label style={{ display: "block", marginBottom: 4 }}>
                localName 검색
              </label>
              <input
                type="text"
                value={localNameKeyword}
                onChange={(e) => setLocalNameKeyword(e.target.value)}
                style={{ padding: 8, width: 160 }}
                placeholder="예: 설날"
              />
            </div>

            <div>
              <label style={{ display: "block", marginBottom: 4 }}>
                English name 검색
              </label>
              <input
                type="text"
                value={englishNameKeyword}
                onChange={(e) => setEnglishNameKeyword(e.target.value)}
                style={{ padding: 8, width: 180 }}
                placeholder="예: New Year"
              />
            </div>

            <div>
              <label style={{ display: "block", marginBottom: 4 }}>
                고정 공휴일(fixed)
              </label>
              <select
                value={fixedHoliday}
                onChange={(e) => setFixedHoliday(e.target.value)}
                style={{ padding: 8 }}
              >
                <option value="ALL">전체</option>
                <option value="TRUE">예</option>
                <option value="FALSE">아니오</option>
              </select>
            </div>

            <div>
              <label style={{ display: "block", marginBottom: 4 }}>
                전세계 공통(global)
              </label>
              <select
                value={globalHoliday}
                onChange={(e) => setGlobalHoliday(e.target.value)}
                style={{ padding: 8 }}
              >
                <option value="ALL">전체</option>
                <option value="TRUE">예</option>
                <option value="FALSE">아니오</option>
              </select>
            </div>

            <div>
              <label style={{ display: "block", marginBottom: 4 }}>
                launchYear (from)
              </label>
              <input
                type="number"
                value={launchYearFrom}
                onChange={(e) => setLaunchYearFrom(e.target.value)}
                style={{ padding: 8, width: 120 }}
                placeholder="예: 1990"
              />
            </div>

            <div>
              <label style={{ display: "block", marginBottom: 4 }}>
                launchYear (to)
              </label>
              <input
                type="number"
                value={launchYearTo}
                onChange={(e) => setLaunchYearTo(e.target.value)}
                style={{ padding: 8, width: 120 }}
                placeholder="예: 2025"
              />
            </div>
          </>
        )}

        <button type="submit" style={{ padding: "8px 16px", height: 40 }}>
          검색
        </button>
      </form>

      {/* YEAR 모드에서만 삭제/재동기화 버튼 노출 */}
      {isYearMode && (
        <div style={{ display: "flex", gap: 8, marginBottom: 12 }}>
          <button
            type="button"
            onClick={handleRefresh}
            disabled={loading || !selectedCountry || !year}
          >
            재동기화
          </button>
          <button
            type="button"
            onClick={handleDelete}
            disabled={loading || !selectedCountry || !year}
          >
            삭제
          </button>
        </div>
      )}

      {/* 아래 영역: 메시지 + 결과 테이블 (세로 방향 꽉 채우기) */}
      <div
        style={{
          flex: 1,
          display: "flex",
          flexDirection: "column",
          minHeight: 0,
        }}
      >
        {loading && <p>처리 중입니다...</p>}
        {error && <p style={{ color: "red" }}>{error}</p>}
        {infoMessage && <p style={{ color: "green" }}>{infoMessage}</p>}

        {!loading && !error && holidays.length > 0 && (
          <>
            <div style={{ flex: 1, overflowY: "auto" }}>
              <table
                style={{
                  width: "100%",
                  borderCollapse: "collapse",
                  fontSize: 14,
                }}
              >
                <thead>
                  <tr>
                    <th style={thStyle}>날짜</th>
                    <th style={thStyle}>연도</th>
                    <th style={thStyle}>국가코드</th>
                    <th style={thStyle}>현지어 이름</th>
                    <th style={thStyle}>영문 이름</th>
                    <th style={thStyle}>타입</th>
                    <th style={thStyle}>고정 공휴일</th>
                    <th style={thStyle}>전세계 공통</th>
                    <th style={thStyle}>launchYear</th>
                  </tr>
                </thead>
                <tbody>
                  {holidays.map((h) => (
                    <tr key={h.id}>
                      <td style={tdStyle}>{h.date}</td>
                      <td style={tdStyle}>{h.holidayYear}</td>
                      <td style={tdStyle}>{h.countryCode}</td>
                      <td style={tdStyle}>{h.localName}</td>
                      <td style={tdStyle}>{h.englishName}</td>
                      <td style={tdStyle}>{h.types}</td>
                      <td style={tdStyle}>{h.fixedHoliday ? "Y" : "N"}</td>
                      <td style={tdStyle}>{h.globalHoliday ? "Y" : "N"}</td>
                      <td style={tdStyle}>{h.launchYear ?? "-"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div
              style={{
                marginTop: 8,
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                flexWrap: "wrap",
                gap: 8,
              }}
            >
              <div>
                총 {pageInfo.totalElements}건 {"       "}
                {pageInfo.totalPages === 0 ? 0 : pageInfo.page + 1} /{" "}
                {pageInfo.totalPages} 페이지
              </div>
              <div style={{ display: "flex", gap: 8 }}>
                <button onClick={handlePrev} disabled={pageInfo.page === 0}>
                  ◀ 이전
                </button>
                <button
                  onClick={handleNext}
                  disabled={
                    pageInfo.page + 1 >= pageInfo.totalPages ||
                    pageInfo.totalPages === 0
                  }
                >
                  다음 ▶
                </button>
              </div>
            </div>
          </>
        )}

        {!loading && !error && holidays.length === 0 && (
          <p>검색 결과가 없습니다. 필터를 설정하고 검색을 눌러주세요.</p>
        )}
      </div>
    </div>
  );
}

const thStyle = {
  borderBottom: "1px solid #ccc",
  padding: "8px 6px",
  textAlign: "left",
  backgroundColor: "#fafafa",
};

const tdStyle = {
  borderBottom: "1px solid #eee",
  padding: "6px 6px",
};

export default HolidaySearch;
