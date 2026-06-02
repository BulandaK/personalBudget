import { useState, useEffect } from 'react'
import { transactionsAPI, accountsAPI } from '../api'
import { Plus, Trash2, AlertCircle, Check, TrendingUp, TrendingDown, BellRing, X } from 'lucide-react'
import { format } from 'date-fns'
import { pl } from 'date-fns/locale'

const CATEGORIES = ['HOUSING', 'FOOD', 'TRANSPORTATION', 'ENTERTAINMENT', 'SALARY', 'UNCATEGORIZED']
const TRANSACTION_TYPES = ['INCOME', 'EXPENSE']

export default function Transactions({ selectedAccount }) {
  const [transactions, setTransactions] = useState([])
  const [accounts, setAccounts] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [toast, setToast] = useState(null)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [filters, setFilters] = useState({
    category: '',
    from: '',
    to: ''
  })
  const [formData, setFormData] = useState({
    amount: '',
    transactionType: 'EXPENSE',
    category: 'HOUSING',
    description: '',
    date: new Date().toISOString().slice(0, 16),
    accountId: selectedAccount || ''
  })

  useEffect(() => {
    loadAccounts()
  }, [])

  useEffect(() => {
    loadTransactions()
  }, [page, filters.category, filters.from, filters.to])

  useEffect(() => {
    if (selectedAccount) {
      setFormData(prev => ({ ...prev, accountId: selectedAccount }))
    }
  }, [selectedAccount])

  const loadAccounts = async () => {
    try {
      const res = await accountsAPI.getAll()
      setAccounts(res.data)
    } catch (err) {
      console.error(err)
    }
  }

  const loadTransactions = async () => {
    try {
      const params = {
        page,
        size: 10,
        ...(filters.category && { category: filters.category }),
        ...(filters.from && { from: filters.from }),
        ...(filters.to && { to: filters.to })
      }
      const res = await transactionsAPI.getAll(params)
      setTransactions(res.data.content || res.data)
      setTotalPages(res.data.totalPages || 1)
      setError('')
    } catch (err) {
      setError('Nie udało się wczytać transakcji')
    } finally {
      setLoading(false)
    }
  }

  const showBudgetToast = (transaction) => {
    setToast({
      title: 'Przekroczono budżet',
      message: `Kategoria ${transaction.category} przekroczyła limit. Wydano już ${parseFloat(transaction.spentInCategory).toFixed(2)} zł w tym miesiącu.`,
      type: 'warning'
    })

    window.setTimeout(() => {
      setToast(null)
    }, 5000)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!formData.amount || !formData.accountId) {
      setError('Wypełnij wszystkie wymagane pola')
      return
    }

    try {
      const payload = {
        amount: parseFloat(formData.amount),
        transactionType: formData.transactionType,
        category: formData.category,
        description: formData.description,
        date: new Date(formData.date).toISOString(),
        accountId: parseInt(formData.accountId)
      }
      const res = await transactionsAPI.create(payload)
      if (res.data?.budgetExceeded) {
        showBudgetToast(res.data)
      }
      setSuccess('Transakcja dodana pomyślnie!')
      setFormData({
        amount: '',
        transactionType: 'EXPENSE',
        category: 'HOUSING',
        description: '',
        date: new Date().toISOString().slice(0, 16),
        accountId: formData.accountId
      })
      setShowForm(false)
      setTimeout(() => setSuccess(''), 3000)
      setPage(0)
      loadTransactions()
    } catch (err) {
      setError(err.response?.data?.message || 'Nie udało się dodać transakcji')
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('Usunąć tę transakcję?')) return

    try {
      await transactionsAPI.delete(id)
      setSuccess('Transakcja usunięta!')
      setTimeout(() => setSuccess(''), 3000)
      loadTransactions()
    } catch (err) {
      setError('Nie udało się usunąć transakcji')
    }
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">Transakcje</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="flex items-center gap-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white px-6 py-2 rounded-lg hover:shadow-lg transition-shadow font-medium"
        >
          <Plus className="w-5 h-5" />
          Nowa transakcja
        </button>
      </div>

      {/* Budget toast */}
      {toast && (
        <div className="fixed top-5 right-5 z-50 w-[min(92vw,420px)] rounded-2xl border border-amber-200 bg-white shadow-2xl overflow-hidden animate-[slideIn_.2s_ease-out]">
          <div className="flex items-start gap-3 p-4 bg-amber-50">
            <div className="mt-0.5 rounded-full bg-amber-100 p-2 text-amber-700">
              <BellRing className="w-5 h-5" />
            </div>
            <div className="flex-1">
              <div className="flex items-center justify-between gap-4">
                <h3 className="font-semibold text-amber-900">{toast.title}</h3>
                <button
                  onClick={() => setToast(null)}
                  className="rounded-full p-1 text-amber-700 hover:bg-amber-100"
                  aria-label="Zamknij powiadomienie"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
              <p className="mt-1 text-sm text-amber-800">{toast.message}</p>
            </div>
          </div>
        </div>
      )}

      {/* Messages */}
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg flex items-center gap-3 text-red-800">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          {error}
        </div>
      )}
      {success && (
        <div className="p-4 bg-green-50 border border-green-200 rounded-lg flex items-center gap-3 text-green-800">
          <Check className="w-5 h-5 flex-shrink-0" />
          {success}
        </div>
      )}

      {/* Create Form */}
      {showForm && (
        <div className="bg-white rounded-lg shadow-sm p-6 border border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Dodaj transakcję</h3>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Account */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Konto</label>
              <select
                value={formData.accountId}
                onChange={(e) => setFormData({ ...formData, accountId: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Wybierz konto</option>
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>{acc.name}</option>
                ))}
              </select>
            </div>

            {/* Amount */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Kwota (zł)</label>
              <input
                type="number"
                step="0.01"
                min="0"
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                placeholder="0.00"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Type */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Typ</label>
              <select
                value={formData.transactionType}
                onChange={(e) => setFormData({ ...formData, transactionType: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                {TRANSACTION_TYPES.map(type => (
                  <option key={type} value={type}>{type === 'INCOME' ? 'Przychód' : 'Wydatek'}</option>
                ))}
              </select>
            </div>

            {/* Category */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Kategoria</label>
              <select
                value={formData.category}
                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                {CATEGORIES.map(cat => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>

            {/* Date */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Data</label>
              <input
                type="datetime-local"
                value={formData.date}
                onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Description */}
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-2">Opis</label>
              <input
                type="text"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Opcjonalnie: notatka do transakcji"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Buttons */}
            <div className="md:col-span-2 flex gap-2">
              <button
                type="submit"
                className="bg-green-500 text-white px-6 py-2 rounded-lg hover:bg-green-600 transition-colors font-medium"
              >
                Dodaj
              </button>
              <button
                type="button"
                onClick={() => setShowForm(false)}
                className="bg-gray-300 text-gray-700 px-6 py-2 rounded-lg hover:bg-gray-400 transition-colors font-medium"
              >
                Anuluj
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Filters */}
      <div className="bg-white rounded-lg shadow-sm p-4 border border-gray-200">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Kategoria</label>
            <select
              value={filters.category}
              onChange={(e) => {
                setFilters({ ...filters, category: e.target.value })
                setPage(0)
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Wszystkie</option>
              {CATEGORIES.map(cat => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Od daty</label>
            <input
              type="date"
              value={filters.from}
              onChange={(e) => {
                setFilters({ ...filters, from: e.target.value ? e.target.value + 'T00:00:00' : '' })
                setPage(0)
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Do daty</label>
            <input
              type="date"
              value={filters.to ? filters.to.split('T')[0] : ''}
              onChange={(e) => {
                setFilters({ ...filters, to: e.target.value ? e.target.value + 'T23:59:59' : '' })
                setPage(0)
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
      </div>

      {/* Transactions Table */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr className="text-left text-sm font-semibold text-gray-700">
                <th className="px-6 py-3">Data</th>
                <th className="px-6 py-3">Kwota</th>
                <th className="px-6 py-3">Typ</th>
                <th className="px-6 py-3">Konto</th>
                <th className="px-6 py-3">Kategoria</th>
                <th className="px-6 py-3">Opis</th>
                <th className="px-6 py-3">Akcja</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {transactions.map(tx => {
                const accountName = accounts.find(a => a.id === tx.accountId)?.name || 'N/A'
                return (
                <tr key={tx.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-sm text-gray-600">
                    {format(new Date(tx.date), 'dd MMM yyyy HH:mm', { locale: pl })}
                  </td>
                  <td className="px-6 py-4 text-sm font-semibold">
                    <span className={tx.type === 'INCOME' ? 'text-green-600' : 'text-red-600'}>
                      {tx.type === 'INCOME' ? '+' : '-'}{parseFloat(tx.amount).toFixed(2)} zł
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <span className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium ${
                      tx.type === 'INCOME'
                        ? 'bg-green-100 text-green-700'
                        : 'bg-red-100 text-red-700'
                    }`}>
                      {tx.type === 'INCOME' ? <TrendingUp className="w-3 h-3" /> : <TrendingDown className="w-3 h-3" />}
                      {tx.type === 'INCOME' ? 'Przychód' : 'Wydatek'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">{accountName}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{tx.category}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{tx.description || '-'}</td>
                  <td className="px-6 py-4 text-sm">
                    <button
                      onClick={() => handleDelete(tx.id)}
                      className="text-gray-400 hover:text-red-500 transition-colors p-1"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </td>
                </tr>
              )
              })}
            </tbody>
          </table>
        </div>

        {transactions.length === 0 && (
          <div className="text-center py-12 text-gray-500">
            Brak transakcji
          </div>
        )}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2">
          <button
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
            className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
          >
            Poprzednia
          </button>
          <span className="px-4 py-2 text-sm text-gray-600">
            Strona {page + 1} z {totalPages}
          </span>
          <button
            onClick={() => setPage(Math.min(page + 1, totalPages - 1))}
            disabled={page === totalPages - 1}
            className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
          >
            Następna
          </button>
        </div>
      )}
    </div>
  )
}
