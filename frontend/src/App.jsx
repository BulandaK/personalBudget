import { useState } from 'react'
import { BarChart3, Wallet, TrendingUp, Settings } from 'lucide-react'
import Dashboard from './components/Dashboard'
import Accounts from './components/Accounts'
import Transactions from './components/Transactions'
import Budgets from './components/Budgets'

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard')
  const [selectedAccount, setSelectedAccount] = useState(null)

  const handleSelectAccount = (accountId) => {
    setSelectedAccount(accountId)
    setActiveTab('dashboard')
  }

  const tabs = [
    { id: 'dashboard', name: 'Pulpit', icon: BarChart3 },
    { id: 'accounts', name: 'Konta', icon: Wallet },
    { id: 'transactions', name: 'Transakcje', icon: TrendingUp },
    { id: 'budgets', name: 'Budżety', icon: Settings }
  ]

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50">
      {/* Header */}
      <header className="bg-white shadow-sm sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-lg">
                <Wallet className="w-6 h-6 text-white" />
              </div>
              <h1 className="text-2xl font-bold text-gray-900">Personal Budget</h1>
            </div>
            <p className="text-sm text-gray-500">Zarządzaj swoimi finansami </p>
          </div>
        </div>
      </header>

      {/* Navigation */}
      <nav className="bg-white border-b border-gray-200 sticky top-16 z-30">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex gap-8">
            {tabs.map(tab => {
              const Icon = tab.icon
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center gap-2 px-4 py-4 border-b-2 transition-colors ${
                    activeTab === tab.id
                      ? 'border-blue-500 text-blue-600 font-semibold'
                      : 'border-transparent text-gray-600 hover:text-gray-900'
                  }`}
                >
                  <Icon className="w-5 h-5" />
                  {tab.name}
                </button>
              )
            })}
          </div>
        </div>
      </nav>

      {/* Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {activeTab === 'dashboard' && <Dashboard selectedAccount={selectedAccount} onSelectAccount={handleSelectAccount} />}
        {activeTab === 'accounts' && <Accounts onSelectAccount={handleSelectAccount} />}
        {activeTab === 'transactions' && <Transactions selectedAccount={selectedAccount} />}
        {activeTab === 'budgets' && <Budgets selectedAccount={selectedAccount} />}
      </main>
    </div>
  )
}
