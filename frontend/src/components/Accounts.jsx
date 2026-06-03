import { useState, useEffect } from 'react'
import { accountsAPI } from '../api'
import { Plus, Trash2, AlertCircle, Check } from 'lucide-react'

export default function Accounts({ onSelectAccount }) {
  const [accounts, setAccounts] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [formData, setFormData] = useState({ name: '' })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    loadAccounts()
  }, [])

  const loadAccounts = async () => {
    try {
      const res = await accountsAPI.getAll()
      setAccounts(res.data)
      setError('')
    } catch (err) {
      setError('Nie udało się wczytać kont')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!formData.name.trim()) {
      setError('Nazwa konta jest wymagana')
      return
    }

    try {
      await accountsAPI.create({ name: formData.name })
      setSuccess('Konto utworzone pomyślnie!')
      setFormData({ name: '' })
      setShowForm(false)
      setTimeout(() => setSuccess(''), 3000)
      loadAccounts()
    } catch (err) {
      setError(err.response?.data?.message || 'Nie udało się utworzyć konta')
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('Czy na pewno chcesz usunąć to konto?')) return

    try {
      await accountsAPI.delete(id)
      setSuccess('Konto usunięte!')
      setTimeout(() => setSuccess(''), 3000)
      loadAccounts()
    } catch (err) {
      setError(err.response?.data?.message || 'Nie udało się usunąć konta')
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto">
      {/* Header with Create Button */}
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-900">Moje Konta</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="flex items-center gap-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white px-6 py-2 rounded-lg hover:shadow-lg transition-shadow font-medium"
        >
          <Plus className="w-5 h-5" />
          Nowe konto
        </button>
      </div>

      {/* Messages */}
      {error && (
        <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg flex items-center gap-3 text-red-800">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          {error}
        </div>
      )}
      {success && (
        <div className="mb-4 p-4 bg-green-50 border border-green-200 rounded-lg flex items-center gap-3 text-green-800">
          <Check className="w-5 h-5 flex-shrink-0" />
          {success}
        </div>
      )}

      {/* Create Form */}
      {showForm && (
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6 border border-gray-200">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nazwa konta
              </label>
              <input
                type="text"
                value={formData.name}
                onChange={(e) => setFormData({ name: e.target.value })}
                placeholder="np. Moja pierwsza karta"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div className="flex gap-2">
              <button
                type="submit"
                className="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 transition-colors font-medium"
              >
                Utwórz
              </button>
              <button
                type="button"
                onClick={() => setShowForm(false)}
                className="bg-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-400 transition-colors font-medium"
              >
                Anuluj
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Accounts List */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {accounts.map(account => (
          <div
            key={account.id}
            className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow"
          >
            <div className="flex items-start justify-between mb-4">
              <div className="flex-1">
                <h3 className="text-lg font-semibold text-gray-900">{account.name}</h3>
                <p className="text-sm text-gray-500">ID: {account.id}</p>
              </div>
              <button
                onClick={() => handleDelete(account.id)}
                className="text-gray-400 hover:text-red-500 transition-colors p-2 hover:bg-red-50 rounded-lg"
              >
                <Trash2 className="w-5 h-5" />
              </button>
            </div>
            <div className="pt-4 border-t border-gray-200">
              <p className="text-sm text-gray-600 mb-2">Saldo</p>
              <p className="text-3xl font-bold text-gray-900 mb-4">
                {parseFloat(account.balance).toFixed(2)} <span className="text-lg">zł</span>
              </p>
              <button
                onClick={() => onSelectAccount(account)}
                className="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 transition-colors font-medium text-sm"
              >
                Wybierz konto
              </button>
            </div>
          </div>
        ))}
      </div>

      {accounts.length === 0 && (
        <div className="text-center py-12 bg-gray-50 rounded-lg border border-gray-200">
          <p className="text-gray-500 mb-4">Nie masz jeszcze żadnych kont</p>
          <button
            onClick={() => setShowForm(true)}
            className="inline-flex items-center gap-2 bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-600 transition-colors font-medium"
          >
            <Plus className="w-5 h-5" />
            Utwórz pierwsze konto
          </button>
        </div>
      )}
    </div>
  )
}
