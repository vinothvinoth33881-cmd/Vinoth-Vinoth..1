import React, { useState, useEffect } from 'react';
import {
  Brain,
  History,
  BarChart2,
  FileText,
  Activity,
  Sparkles,
  Layers,
  HelpCircle,
  ExternalLink
} from 'lucide-react';
import { CaseRecord } from './types';
import { INITIAL_CASE_RECORDS } from './data/benchmarkCases';
import { DiagnosisScreen } from './screens/DiagnosisScreen';
import { CasesHistoryScreen } from './screens/CasesHistoryScreen';
import { BenchmarksScreen } from './screens/BenchmarksScreen';
import { ClinicalReportScreen } from './screens/ClinicalReportScreen';
import { EducationalGuideModal } from './components/EducationalGuideModal';

type ActiveTab = 'DIAGNOSIS' | 'HISTORY' | 'BENCHMARKS' | 'REPORT';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState<ActiveTab>('DIAGNOSIS');
  const [cases, setCases] = useState<CaseRecord[]>(() => {
    try {
      const saved = localStorage.getItem('neuroscan_cases');
      if (saved) {
        return JSON.parse(saved);
      }
    } catch (e) {
      console.error(e);
    }
    return INITIAL_CASE_RECORDS;
  });

  const [selectedReportCase, setSelectedReportCase] = useState<CaseRecord>(cases[0] || INITIAL_CASE_RECORDS[0]);
  const [isGuideOpen, setIsGuideOpen] = useState<boolean>(false);

  // Sync to localStorage
  useEffect(() => {
    try {
      localStorage.setItem('neuroscan_cases', JSON.stringify(cases));
    } catch (e) {
      console.error(e);
    }
  }, [cases]);

  const handleSaveCase = (newCase: CaseRecord) => {
    setCases((prev) => [newCase, ...prev.filter((c) => c.patientId !== newCase.patientId)]);
    setSelectedReportCase(newCase);
  };

  const handleDeleteCase = (id: string) => {
    setCases((prev) => prev.filter((c) => c.id !== id));
  };

  const handleViewReport = (caseRecord: CaseRecord) => {
    setSelectedReportCase(caseRecord);
    setActiveTab('REPORT');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleUpdateRecord = (updated: CaseRecord) => {
    setSelectedReportCase(updated);
    setCases((prev) => prev.map((c) => (c.id === updated.id ? updated : c)));
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        backgroundColor: '#040914',
        color: '#f1f5f9',
        display: 'flex',
        flexDirection: 'column',
        fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, sans-serif"
      }}
    >
      {/* Top Application Navigation Bar */}
      <header
        style={{
          position: 'sticky',
          top: 0,
          zIndex: 100,
          backgroundColor: 'rgba(9, 15, 29, 0.95)',
          backdropFilter: 'blur(10px)',
          borderBottom: '1px solid #1e293b',
          padding: '0 16px'
        }}
      >
        <div
          style={{
            maxWidth: '1080px',
            margin: '0 auto',
            height: '60px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}
        >
          {/* App Logo & Title */}
          <div
            onClick={() => setActiveTab('DIAGNOSIS')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              cursor: 'pointer'
            }}
          >
            <div
              style={{
                width: '36px',
                height: '36px',
                borderRadius: '10px',
                overflow: 'hidden',
                border: '1.5px solid #38bdf8',
                backgroundColor: '#0b192e',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              <img
                src="/ic_kidney_logo.jpg"
                alt="NeuroScan Logo"
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                onError={(e) => {
                  // fallback icon if image fails
                  (e.target as HTMLElement).style.display = 'none';
                }}
              />
            </div>

            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <span style={{ fontSize: '16px', fontWeight: 800, color: '#f8fafc', letterSpacing: '-0.3px' }}>
                  NeuroScan<span style={{ color: '#38bdf8' }}>AI</span>
                </span>
                <span
                  style={{
                    fontSize: '9px',
                    fontWeight: 700,
                    backgroundColor: 'rgba(56, 189, 248, 0.15)',
                    color: '#38bdf8',
                    padding: '2px 6px',
                    borderRadius: '4px'
                  }}
                >
                  v2.6 CDS
                </span>
              </div>
              <div style={{ fontSize: '10px', color: '#64748b' }}>
                Kidney Disease Deep Learning System
              </div>
            </div>
          </div>

          {/* Navigation Links */}
          <nav style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
            <button
              onClick={() => setActiveTab('DIAGNOSIS')}
              style={{
                backgroundColor: activeTab === 'DIAGNOSIS' ? '#1e293b' : 'transparent',
                border: `1px solid ${activeTab === 'DIAGNOSIS' ? '#38bdf8' : 'transparent'}`,
                borderRadius: '8px',
                color: activeTab === 'DIAGNOSIS' ? '#38bdf8' : '#94a3b8',
                padding: '6px 12px',
                fontSize: '12px',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                cursor: 'pointer'
              }}
            >
              <Activity size={15} />
              <span className="nav-label">Diagnosis</span>
            </button>

            <button
              onClick={() => setActiveTab('HISTORY')}
              style={{
                backgroundColor: activeTab === 'HISTORY' ? '#1e293b' : 'transparent',
                border: `1px solid ${activeTab === 'HISTORY' ? '#38bdf8' : 'transparent'}`,
                borderRadius: '8px',
                color: activeTab === 'HISTORY' ? '#38bdf8' : '#94a3b8',
                padding: '6px 12px',
                fontSize: '12px',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                cursor: 'pointer'
              }}
            >
              <History size={15} />
              <span className="nav-label">Cases ({cases.length})</span>
            </button>

            <button
              onClick={() => setActiveTab('BENCHMARKS')}
              style={{
                backgroundColor: activeTab === 'BENCHMARKS' ? '#1e293b' : 'transparent',
                border: `1px solid ${activeTab === 'BENCHMARKS' ? '#38bdf8' : 'transparent'}`,
                borderRadius: '8px',
                color: activeTab === 'BENCHMARKS' ? '#38bdf8' : '#94a3b8',
                padding: '6px 12px',
                fontSize: '12px',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                cursor: 'pointer'
              }}
            >
              <BarChart2 size={15} />
              <span className="nav-label">Benchmarks</span>
            </button>

            <button
              onClick={() => setActiveTab('REPORT')}
              style={{
                backgroundColor: activeTab === 'REPORT' ? '#1e293b' : 'transparent',
                border: `1px solid ${activeTab === 'REPORT' ? '#38bdf8' : 'transparent'}`,
                borderRadius: '8px',
                color: activeTab === 'REPORT' ? '#38bdf8' : '#94a3b8',
                padding: '6px 12px',
                fontSize: '12px',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                cursor: 'pointer'
              }}
            >
              <FileText size={15} />
              <span className="nav-label">Report</span>
            </button>
          </nav>
        </div>
      </header>

      {/* Main Content Area */}
      <main
        style={{
          flex: 1,
          maxWidth: '1080px',
          width: '100%',
          margin: '0 auto',
          padding: '16px 16px 80px 16px',
          boxSizing: 'border-box'
        }}
      >
        {activeTab === 'DIAGNOSIS' && (
          <DiagnosisScreen
            onSaveCase={handleSaveCase}
            onViewReport={handleViewReport}
            onOpenGuide={() => setIsGuideOpen(true)}
          />
        )}

        {activeTab === 'HISTORY' && (
          <CasesHistoryScreen
            cases={cases}
            onDeleteCase={handleDeleteCase}
            onViewReport={handleViewReport}
          />
        )}

        {activeTab === 'BENCHMARKS' && <BenchmarksScreen />}

        {activeTab === 'REPORT' && (
          <ClinicalReportScreen
            record={selectedReportCase}
            onUpdateRecord={handleUpdateRecord}
            onBack={() => setActiveTab('DIAGNOSIS')}
          />
        )}
      </main>

      {/* Educational Guide Modal Popup */}
      <EducationalGuideModal isOpen={isGuideOpen} onClose={() => setIsGuideOpen(false)} />

      {/* Footer */}
      <footer
        style={{
          borderTop: '1px solid #1e293b',
          backgroundColor: '#090f1d',
          padding: '16px',
          textAlign: 'center',
          fontSize: '11px',
          color: '#64748b'
        }}
      >
        <div style={{ maxWidth: '1080px', margin: '0 auto' }}>
          <div>
            NeuroScan AI • Center for Medical Image Diagnosis & Machine Learning in Nephrology
          </div>
          <div style={{ marginTop: '4px', fontSize: '10px', color: '#475569' }}>
            Academic clinical decision-support prototype. Strictly for research and educational purposes.
          </div>
        </div>
      </footer>
    </div>
  );
};
