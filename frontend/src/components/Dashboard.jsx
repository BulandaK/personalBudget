import { useState, useEffect } from 'react'
import { accountsAPI, analyticsAPI } from '../api'
import {
  TrendingDown,
  TrendingUp,
  DollarSign,
  AlertCircle,
  Wallet,
  CalendarRange,
  PieChart,
} from 'lucide-react'
import { format } from 'date-fns'
import { pl } from 'date-fns/locale'

export default function Dashboard({ selectedAccount, onSelectAccount }) {
  const [accounts, setAccounts] = useState([])
  const [analytics, setAnalytics] = useState(null)
  const [loading, setLoading] = useState(true)
  const [analyticsLoading, setAnalyticsLoading] = useState(false)
  const [analyticsError, setAnalyticsError] = useState('')
  const [dateRange, setDateRange] = useState('month')
  const [customFrom, setCustomFrom] = useState('')
  const [customTo, setCustomTo] = useState('')

  useEffect(() => {
    loadAccounts()
  }, [])

  useEffect(() => {
    if (selectedAccount) {
      loadAnalytics(selectedAccount)
    } else {
      setAnalytics(null)
      setAnalyticsError('')
    }
  }, [selectedAccount, dateRange])

  const loadAccounts = async () => {
    try {
      const res = await accountsAPI.getAll()
      setAccounts(res.data)
      setLoading(false)
    } catch (err) {
      console.error('Error loading accounts:', err)
      setLoading(false)
    }
  }

  const getDateRange = () => {
    if (dateRange === 'custom' && customFrom && customTo) {
      return [new Date(customFrom), new Date(customTo)]
    }

    const now = new Date()
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
    const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59)

    if (dateRange === 'week') {
      const startOfWeek = new Date(now)
      startOfWeek.setDate(now.getDate() - now.getDay())
      return [startOfWeek, now]
    }
    return [startOfMonth, endOfMonth]
  }

  const loadAnalytics = async (accountId) => {
    try {
      setAnalyticsLoading(true)
      setAnalyticsError('')
      const [from, to] = getDateRange()
      const res = await analyticsAPI.getAccountAnalytics(
        accountId,
        from.toISOString(),
        to.toISOString()
      )
      setAnalytics(res.data)
    } catch (err) {
      setAnalyticsError('Nie udało się wczytać analityki dla wybranego konta')
      console.error('Error loading analytics:', err)
    } finally {
      setAnalyticsLoading(false)
    }
  }

  const applyCustomRange = () => {
    if (!customFrom || !customTo) {
      setAnalyticsError('Ustaw datę od i do')
      return
    }

    if (selectedAccount) {
      loadAnalytics(selectedAccount)
    }
  }

  const selectedAccountData = accounts.find(account => String(account.id) === String(selectedAccount))

  const periodLabel = analytics?.period
    ? `${format(new Date(analytics.period.fromDate), 'dd MMM yyyy', { locale: pl })} — ${format(new Date(analytics.period.toDate), 'dd MMM yyyy', { locale: pl })}`
    : ''

  const balance = analytics
    ? parseFloat(analytics.totalIncome) - parseFloat(analytics.totalExpenses)
    : 0

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {accounts.map(account => {
          const isActive = String(account.id) === String(selectedAccount)
          return (
            <button
              key={account.id}
              onClick={() => onSelectAccount(account)}
              className={`text-left bg-white rounded-2xl shadow-sm p-5 border transition-all hover:shadow-md ${
                isActive ? 'border-blue-500 ring-2 ring-blue-100' : 'border-gray-200'
              }`}
            >
              <div className="flex items-start justify-between gap-4">
                <div>
                  <p className="text-xs uppercase tracking-wide text-gray-500">Konto</p>
                  <h3 className="text-lg font-semibold text-gray-900 mt-1">{account.name}</h3>
                </div>
                <div className="p-2 rounded-xl bg-blue-50 text-blue-600">
                  <Wallet className="w-5 h-5" />
                </div>
              </div>
              <p className="text-3xl font-bold text-gray-900 mt-4">
                {parseFloat(account.balance).toFixed(2)} <span className="text-base font-medium text-gray-500">zł</span>
              </p>
              <p className="text-sm text-gray-500 mt-2">ID: {account.id}</p>
            </button>
          )
        })}
      </div>

      {!selectedAccount ? (
        <div className="bg-blue-50 border border-blue-200 rounded-2xl p-6 flex items-center gap-4">
          <AlertCircle className="w-6 h-6 text-blue-600 flex-shrink-0" />
          <p className="text-blue-800">Wybierz konto z listy, a poniżej pojawi się elegancki podgląd analityki.</p>
        </div>
      ) : (
        <div className="space-y-6">
          <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-200">
            <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
              <div>
                <p className="text-xs uppercase tracking-wide text-gray-500">Wybrane konto</p>
                <h2 className="text-2xl font-bold text-gray-900 mt-1">
                  {selectedAccountData?.name || `Konto #${selectedAccount}`}
                </h2>
                <p className="text-sm text-gray-500 mt-2 flex items-center gap-2">
                  <CalendarRange className="w-4 h-4" />
                  {periodLabel || 'Ładowanie zakresu danych...'}
                </p>
              </div>

              <div className="flex flex-wrap gap-2">
                {['week', 'month'].map(range => (
                  <button
                    key={range}
                    onClick={() => setDateRange(range)}
                    className={`px-4 py-2 rounded-xl font-medium text-sm transition-colors ${
                      dateRange === range
                        ? 'bg-blue-600 text-white'
                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    }`}
                  >
                    {range === 'week' ? 'Ostatni tydzień' : 'Bieżący miesiąc'}
                  </button>
                ))}
                <button
                  onClick={() => setDateRange('custom')}
                  className={`px-4 py-2 rounded-xl font-medium text-sm transition-colors ${
                    dateRange === 'custom'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Własny zakres
                </button>
              </div>
            </div>

            {dateRange === 'custom' && (
              <div className="mt-4 grid grid-cols-1 md:grid-cols-3 gap-3 items-end">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Od</label>
                  <input
                    type="date"
                    value={customFrom}
                    onChange={(e) => setCustomFrom(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Do</label>
                  <input
                    type="date"
                    value={customTo}
                    onChange={(e) => setCustomTo(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <button
                  onClick={applyCustomRange}
                  className="px-4 py-2 rounded-xl bg-blue-600 text-white font-medium hover:bg-blue-700 transition-colors"
                >
                  Pokaż analizę
                </button>
              </div>
            )}
          </div>

          {analyticsLoading && (
            <div className="flex items-center justify-center py-12">
              <div className="h-12 w-12 rounded-full border-4 border-blue-200 border-t-blue-600 animate-spin" />
            </div>
          )}

          {analyticsError && !analyticsLoading && (
            <div className="bg-red-50 border border-red-200 rounded-2xl p-6 flex items-center gap-4 text-red-800">
              <AlertCircle className="w-6 h-6 flex-shrink-0" />
              <p>{analyticsError}</p>
            </div>
          )}

          {analytics && !analyticsLoading && !analyticsError && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-gradient-to-br from-green-50 to-emerald-50 rounded-2xl shadow-sm p-6 border border-green-100">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-green-600 font-medium">Przychody</p>
                      <p className="text-3xl font-bold text-green-700 mt-2">
                        {parseFloat(analytics.totalIncome).toFixed(2)} zł
                      </p>
                    </div>
                    <TrendingUp className="w-12 h-12 text-green-400 opacity-50" />
                  </div>
                </div>

                <div className="bg-gradient-to-br from-red-50 to-pink-50 rounded-2xl shadow-sm p-6 border border-red-100">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-red-600 font-medium">Wydatki</p>
                      <p className="text-3xl font-bold text-red-700 mt-2">
                        {parseFloat(analytics.totalExpenses).toFixed(2)} zł
                      </p>
                    </div>
                    <TrendingDown className="w-12 h-12 text-red-400 opacity-50" />
                  </div>
                </div>

                <div className={`rounded-2xl shadow-sm p-6 border ${balance >= 0 ? 'bg-gradient-to-br from-blue-50 to-indigo-50 border-blue-100' : 'bg-gradient-to-br from-orange-50 to-rose-50 border-orange-100'}`}>
                  <div className="flex items-center justify-between">
                    <div>
                      <p className={`text-sm font-medium ${balance >= 0 ? 'text-blue-600' : 'text-orange-600'}`}>Bilans</p>
                      <p className={`text-3xl font-bold mt-2 ${balance >= 0 ? 'text-blue-700' : 'text-orange-700'}`}>
                        {balance.toFixed(2)} zł
                      </p>
                    </div>
                    <DollarSign className={`w-12 h-12 opacity-50 ${balance >= 0 ? 'text-blue-400' : 'text-orange-400'}`} />
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm p-6 border border-gray-200">
                  <div className="flex items-center gap-2 mb-5">
                    <PieChart className="w-5 h-5 text-blue-600" />
                    <h3 className="text-lg font-semibold text-gray-900">Wydatki wg kategorii</h3>
                  </div>

                  {analytics.expensesByCategory?.length ? (
                    <div className="space-y-4">
                      {analytics.expensesByCategory.map((item) => (
                        <div key={item.category} className="space-y-2">
                          <div className="flex items-center justify-between gap-4">
                            <div>
                              <p className="font-medium text-gray-900">{item.category}</p>
                              <p className="text-sm text-gray-500">{parseFloat(item.amount).toFixed(2)} zł</p>
                            </div>
                            <div className="text-right">
                              <p className="font-semibold text-gray-900">{item.percentage.toFixed(1)}%</p>
                            </div>
                          </div>
                          <div className="h-3 rounded-full bg-gray-100 overflow-hidden">
                            <div
                              className="h-3 rounded-full bg-gradient-to-r from-blue-500 to-indigo-600"
                              style={{ width: `${Math.min(Number(item.percentage), 100)}%` }}
                            />
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="text-sm text-gray-500">Brak danych o wydatkach w wybranym zakresie.</div>
                  )}
                </div>

                <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-200">
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Podsumowanie</h3>
                  <div className="space-y-4">
                    <div className="p-4 rounded-xl bg-gray-50">
                      <p className="text-sm text-gray-500">Numer konta</p>
                      <p className="text-lg font-semibold text-gray-900">{analytics.accountId}</p>
                    </div>
                    <div className="p-4 rounded-xl bg-gray-50">
                      <p className="text-sm text-gray-500">Zakres analizy</p>
                      <p className="text-sm font-medium text-gray-900 leading-6">{periodLabel}</p>
                    </div>
                    <div className="p-4 rounded-xl bg-gray-50">
                      <p className="text-sm text-gray-500">Liczba kategorii</p>
                      <p className="text-lg font-semibold text-gray-900">{analytics.expensesByCategory?.length || 0}</p>
                    </div>
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      )}
    </div>
  )
}
