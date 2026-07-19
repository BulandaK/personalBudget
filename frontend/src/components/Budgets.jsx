import { useState, useEffect } from 'react'
import { budgetsAPI, accountsAPI } from '../api'
import { Plus, Trash2, Edit2, AlertCircle, Check } from 'lucide-react'

const CATEGORIES = ['FOOD', 'TRANSPORT', 'ENTERTAINMENT', 'UTILITIES', 'HEALTH', 'SHOPPING', 'OTHER']

export default function Budgets({ selectedAccount }) {
  const [budgets, setBudgets] = useState([])
  const [accounts, setAccounts] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [formData, setFormData] = useState({
    accountId: selectedAccount || '',
    category: 'FOOD',
    limitAmount: ''
  })

  useEffect(() => {
    loadAccounts()
  }, [])

  useEffect(() => {
    if (selectedAccount) {
      setFormData(prev => ({ ...prev, accountId: selectedAccount }))
      loadBudgets(selectedAccount)
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

  const loadBudgets = async (accountId) => {
    try {
      const res = await budgetsAPI.getByAccount(accountId)
      setBudgets(res.data)
      setError('')
      setLoading(false)
    } catch (err) {
      setError('Nie udało się wczytać budżetów')
      setLoading(false)
    }
  }

  const handleAccountChange = (e) => {
    const accountId = e.target.value
    setFormData(prev => ({ ...prev, accountId }))
    if (accountId) {
      loadBudgets(accountId)
    } else {
      setBudgets([])
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!formData.accountId || !formData.limitAmount) {
      setError('Wypełnij wszystkie pola')
      return
    }

    try {
      const payload = {
        accountId: parseInt(formData.accountId),
        category: formData.category,
        limitAmount: parseFloat(formData.limitAmount)
      }

      if (editingId) {
        await budgetsAPI.update(payload)
        setSuccess('Budżet zaktualizowany!')
      } else {
        await budgetsAPI.create(payload)
        setSuccess('Budżet utworzony!')
      }

      setFormData({
        accountId: formData.accountId,
        category: 'FOOD',
        limitAmount: ''
      })
      setEditingId(null)
      setShowForm(false)
      setTimeout(() => setSuccess(''), 3000)
      loadBudgets(formData.accountId)
    } catch (err) {
      setError(err.response?.data?.message || 'Não foi possível salvar o orçamento')
    }
  }

  const handleEdit = (budget) => {
    setFormData({
      accountId: budget.accountId,
      category: budget.category,
      limitAmount: budget.limitAmount
    })
    setEditingId(budget.id)
    setShowForm(true)
  }

  const handleDelete = async (id) => {
    if (!confirm('Usunąć ten budżet?')) return

    try {
      await budgetsAPI.delete(id)
      setSuccess('Budżet usunięty!')
      setTimeout(() => setSuccess(''), 3000)
      loadBudgets(formData.accountId)
    } catch (err) {
      setError('Nie udało się usunąć budżetu')
    }
  }

  if (!selectedAccount && !formData.accountId) {
    return (
      <div className="max-w-6xl mx-auto">
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 flex items-center gap-4">
          <AlertCircle className="w-6 h-6 text-blue-600 flex-shrink-0" />
          <p className="text-blue-800">Przejdź do sekcji "Konta" i wybierz konto, aby zarządzać budżetami</p>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">Budżety kategorii</h2>
        <button
          onClick={() => {
            setEditingId(null)
            setFormData({
              accountId: formData.accountId,
              category: 'FOOD',
              limitAmount: ''
            })
            setShowForm(!showForm)
          }}
          className="flex items-center gap-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white px-6 py-2 rounded-lg hover:shadow-lg transition-shadow font-medium"
        >
          <Plus className="w-5 h-5" />
          {editingId ? 'Anuluj edycję' : 'Nowy budżet'}
        </button>
      </div>

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

      {/* Form */}
      {showForm && (
        <div className="bg-white rounded-lg shadow-sm p-6 border border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            {editingId ? 'Edytuj budżet' : 'Utwórz nowy budżet'}
          </h3>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* Account */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Konto</label>
              <select
                value={formData.accountId}
                onChange={handleAccountChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Wybierz konto</option>
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>{acc.name}</option>
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

            {/* Limit Amount */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Limit (zł)</label>
              <input
                type="number"
                step="0.01"
                min="0"
                value={formData.limitAmount}
                onChange={(e) => setFormData({ ...formData, limitAmount: e.target.value })}
                placeholder="0.00"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Buttons */}
            <div className="md:col-span-3 flex gap-2">
              <button
                type="submit"
                className="bg-green-500 text-white px-6 py-2 rounded-lg hover:bg-green-600 transition-colors font-medium"
              >
                {editingId ? 'Aktualizuj' : 'Utwórz'}
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowForm(false)
                  setEditingId(null)
                }}
                className="bg-gray-300 text-gray-700 px-6 py-2 rounded-lg hover:bg-gray-400 transition-colors font-medium"
              >
                Anuluj
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Budgets Grid */}
      {loading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {budgets.map(budget => (
            <div
              key={budget.id}
              className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow"
            >
              <div className="flex items-start justify-between mb-4">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">{budget.category}</h3>
                  <p className="text-sm text-gray-500">Limit budżetu</p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => handleEdit(budget)}
                    className="text-gray-400 hover:text-blue-500 transition-colors p-2 hover:bg-blue-50 rounded-lg"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDelete(budget.id)}
                    className="text-gray-400 hover:text-red-500 transition-colors p-2 hover:bg-red-50 rounded-lg"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
              <p className="text-3xl font-bold text-blue-600 mb-4">
                {parseFloat(budget.limitAmount).toFixed(2)} <span className="text-lg">zł</span>
              </p>
              <div className="pt-4 border-t border-gray-200">
                <p className="text-xs text-gray-500">ID: {budget.id} | Konto: {budget.accountId}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      {budgets.length === 0 && !loading && (
        <div className="text-center py-12 bg-gray-50 rounded-lg border border-gray-200">
          <p className="text-gray-500 mb-4">Nie masz jeszcze żadnych budżetów dla tego konta</p>
          <button
            onClick={() => setShowForm(true)}
            className="inline-flex items-center gap-2 bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-600 transition-colors font-medium"
          >
            <Plus className="w-5 h-5" />
            Utwórz pierwszy budżet
          </button>
        </div>
      )}
    </div>
  )
}
