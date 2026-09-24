import React, { useState } from 'react';
import { Search, Trash2, FileText, CheckCircle, Clock, Filter, User } from 'lucide-react';
import { CaseRecord, DIAGNOSIS_CLASSES, DiagnosisClass } from '../types';

interface CasesHistoryScreenProps {
  cases: CaseRecord[];
  onDeleteCase: (id: string) => void;
  onViewReport: (record: CaseRecord) => void;
}

export const CasesHistoryScreen: React.FC<CasesHistoryScreenProps> = ({
  cases,
  onDeleteCase,
  onViewReport
}) => {
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [selectedFilter, setSelectedFilter] = useState<string>('ALL');

  const filteredCases = cases.filter((c) => {
    const matchesSearch =
      c.patientId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.primaryDiagnosis.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.symptoms.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.activeModel.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesFilter =
      selectedFilter === 'ALL' || c.primaryDiagnosis === selectedFilter;

    return matchesSearch && matchesFilter;
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Screen Header */}
      <div>
        <h1 style={{ margin: 0, fontSize: '20px', fontWeight: 800, color: '#ffffff' }}>
          Diagnostic Archive & Case History
        </h1>
        <div style={{ fontSize: '11.5px', color: '#94a3b8' }}>
          Archived Patient CT Scans, Multi-Model Predictions, and Physician Reviews
        </div>
      </div>

      {/* Search and Category Filter Chips */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {/* Search input */}
        <div
          style={{
            position: 'relative',
            display: 'flex',
            alignItems: 'center'
          }}
        >
          <Search size={16} color="#64748b" style={{ position: 'absolute', left: '12px' }} />
          <input
            type="text"
            placeholder="Search patient ID, pathology, symptoms..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              width: '100%',
              backgroundColor: '#0d1527',
              border: '1px solid #1e293b',
              borderRadius: '10px',
              padding: '10px 12px 10px 36px',
              color: '#f8fafc',
              fontSize: '12px',
              boxSizing: 'border-box'
            }}
          />
        </div>

        {/* Filter chips */}
        <div style={{ display: 'flex', gap: '6px', overflowX: 'auto', paddingBottom: '2px' }}>
          {['ALL', 'TUMOR', 'STONE', 'CYST', 'NORMAL'].map((f) => {
            const isSelected = selectedFilter === f;
            const cls = f !== 'ALL' ? DIAGNOSIS_CLASSES[f as DiagnosisClass] : null;
            return (
              <button
                key={f}
                onClick={() => setSelectedFilter(f)}
                style={{
                  backgroundColor: isSelected ? 'rgba(56, 189, 248, 0.2)' : '#0d1527',
                  border: `1px solid ${isSelected ? '#38bdf8' : '#1e293b'}`,
                  borderRadius: '20px',
                  color: isSelected ? '#38bdf8' : '#94a3b8',
                  padding: '5px 12px',
                  fontSize: '11px',
                  fontWeight: 600,
                  whiteSpace: 'nowrap',
                  cursor: 'pointer'
                }}
              >
                {cls ? cls.displayName : 'All Pathology'}
              </button>
            );
          })}
        </div>
      </div>

      {/* Cases List */}
      {filteredCases.length === 0 ? (
        <div
          style={{
            backgroundColor: '#090f1d',
            border: '1px dashed #1e293b',
            borderRadius: '14px',
            padding: '36px',
            textAlign: 'center',
            color: '#64748b',
            fontSize: '13px'
          }}
        >
          No diagnostic cases match the current filter or search criteria.
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          {filteredCases.map((c) => {
            const cls = DIAGNOSIS_CLASSES[c.primaryDiagnosis];
            const dateStr = new Date(c.timestamp).toLocaleDateString('en-US', {
              month: 'short',
              day: 'numeric',
              hour: '2-digit',
              minute: '2-digit'
            });

            return (
              <div
                key={c.id}
                style={{
                  backgroundColor: '#0d1527',
                  border: '1px solid #1e293b',
                  borderRadius: '14px',
                  padding: '14px',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '10px',
                  transition: 'border-color 0.15s ease'
                }}
              >
                {/* Header row: Patient ID, Classification badge, delete */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <div
                      style={{
                        width: '28px',
                        height: '28px',
                        borderRadius: '6px',
                        backgroundColor: '#1e293b',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                      }}
                    >
                      <User size={14} color="#38bdf8" />
                    </div>
                    <div>
                      <div style={{ fontSize: '13px', fontWeight: 700, color: '#f8fafc', fontFamily: 'monospace' }}>
                        {c.patientId}
                      </div>
                      <div style={{ fontSize: '10px', color: '#64748b' }}>
                        {c.patientAge}y {c.patientGender} • {dateStr}
                      </div>
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span
                      style={{
                        fontSize: '10px',
                        fontWeight: 700,
                        color: cls.color,
                        backgroundColor: cls.badgeBg,
                        padding: '3px 8px',
                        borderRadius: '6px'
                      }}
                    >
                      {cls.displayName} ({(c.confidence * 100).toFixed(1)}%)
                    </span>

                    <button
                      onClick={() => onDeleteCase(c.id)}
                      style={{
                        backgroundColor: 'transparent',
                        border: 'none',
                        color: '#64748b',
                        cursor: 'pointer',
                        padding: '4px',
                        borderRadius: '4px'
                      }}
                      title="Delete record"
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                </div>

                {/* Symptoms and Labs summary */}
                <div style={{ fontSize: '11px', color: '#cbd5e1', lineHeight: 1.4 }}>
                  <strong>Presentation:</strong> {c.symptoms}
                </div>

                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(100px, 1fr))',
                    gap: '6px',
                    backgroundColor: '#090f1d',
                    padding: '8px 10px',
                    borderRadius: '8px',
                    fontSize: '10px',
                    color: '#94a3b8'
                  }}
                >
                  <div>Model: <strong style={{ color: '#f8fafc' }}>{c.activeModel}</strong></div>
                  <div>eGFR: <strong style={{ color: '#f8fafc' }}>{c.egfr}</strong></div>
                  <div>Creatinine: <strong style={{ color: '#f8fafc' }}>{c.serumCreatinine}</strong></div>
                  <div>BP: <strong style={{ color: '#f8fafc' }}>{c.bloodPressure}</strong></div>
                </div>

                {/* Verification status and View Report button */}
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    paddingTop: '6px',
                    borderTop: '1px solid rgba(255, 255, 255, 0.05)'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '5px', fontSize: '10.5px' }}>
                    {c.reviewStatus.includes('Verified') ? (
                      <>
                        <CheckCircle size={13} color="#10b981" />
                        <span style={{ color: '#10b981', fontWeight: 600 }}>{c.reviewStatus}</span>
                      </>
                    ) : (
                      <>
                        <Clock size={13} color="#f59e0b" />
                        <span style={{ color: '#f59e0b', fontWeight: 600 }}>{c.reviewStatus}</span>
                      </>
                    )}
                  </div>

                  <button
                    onClick={() => onViewReport(c)}
                    style={{
                      backgroundColor: '#1e293b',
                      border: '1px solid #38bdf8',
                      borderRadius: '6px',
                      color: '#38bdf8',
                      padding: '5px 10px',
                      fontSize: '11px',
                      fontWeight: 600,
                      display: 'flex',
                      alignItems: 'center',
                      gap: '4px',
                      cursor: 'pointer'
                    }}
                  >
                    <FileText size={13} />
                    View Report
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
