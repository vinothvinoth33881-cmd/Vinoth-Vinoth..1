import React from 'react';
import { X, BookOpen, CheckCircle, AlertOctagon } from 'lucide-react';
import { DIAGNOSIS_CLASSES, DiagnosisClass } from '../types';

interface EducationalGuideModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const EducationalGuideModal: React.FC<EducationalGuideModalProps> = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.75)',
        backdropFilter: 'blur(4px)',
        zIndex: 1000,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '16px'
      }}
      onClick={onClose}
    >
      <div
        style={{
          backgroundColor: '#0a1122',
          border: '1px solid #1e293b',
          borderRadius: '16px',
          width: '100%',
          maxWidth: '620px',
          maxHeight: '90vh',
          display: 'flex',
          flexDirection: 'column',
          boxShadow: '0 20px 40px rgba(0, 0, 0, 0.8)',
          overflow: 'hidden'
        }}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Modal Header */}
        <div
          style={{
            padding: '16px 20px',
            backgroundColor: '#0f172a',
            borderBottom: '1px solid #1e293b',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div
              style={{
                width: '36px',
                height: '36px',
                borderRadius: '8px',
                backgroundColor: 'rgba(56, 189, 248, 0.15)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              <BookOpen size={20} color="#38bdf8" />
            </div>
            <div>
              <div style={{ fontSize: '15px', fontWeight: 700, color: '#f8fafc' }}>
                Kidney Disease Atlas & Diagnostic Criteria
              </div>
              <div style={{ fontSize: '11px', color: '#38bdf8' }}>
                Radiological Signs & Clinical Triage Protocols
              </div>
            </div>
          </div>

          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              color: '#94a3b8',
              cursor: 'pointer',
              padding: '6px',
              borderRadius: '6px'
            }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Modal Body */}
        <div
          style={{
            padding: '20px',
            overflowY: 'auto',
            display: 'flex',
            flexDirection: 'column',
            gap: '14px'
          }}
        >
          {(Object.keys(DIAGNOSIS_CLASSES) as DiagnosisClass[]).map((clsKey) => {
            const item = DIAGNOSIS_CLASSES[clsKey];
            return (
              <div
                key={clsKey}
                style={{
                  backgroundColor: '#0f172a',
                  borderRadius: '12px',
                  border: `1px solid ${item.color}40`,
                  padding: '14px',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '8px'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span
                    style={{
                      width: '10px',
                      height: '10px',
                      borderRadius: '50%',
                      backgroundColor: item.color,
                      display: 'inline-block'
                    }}
                  />
                  <span style={{ fontSize: '14px', fontWeight: 700, color: item.color }}>
                    {item.scientificName}
                  </span>
                </div>

                <div>
                  <div style={{ fontSize: '10.5px', fontWeight: 600, color: '#cbd5e1', marginBottom: '2px' }}>
                    Radiological CT Presentation:
                  </div>
                  <div style={{ fontSize: '11.5px', color: '#94a3b8', lineHeight: 1.5 }}>
                    {item.summary}
                  </div>
                </div>

                <div>
                  <div style={{ fontSize: '10.5px', fontWeight: 600, color: '#cbd5e1', marginBottom: '2px' }}>
                    Clinical Triage & Decision Protocol:
                  </div>
                  <div style={{ fontSize: '11.5px', color: '#94a3b8', lineHeight: 1.5 }}>
                    {item.clinicalGuidance}
                  </div>
                </div>
              </div>
            );
          })}

          <div
            style={{
              backgroundColor: 'rgba(56, 189, 248, 0.08)',
              border: '1px solid rgba(56, 189, 248, 0.2)',
              borderRadius: '10px',
              padding: '12px',
              fontSize: '11px',
              color: '#94a3b8',
              lineHeight: 1.5
            }}
          >
            <strong style={{ color: '#38bdf8' }}>Hounsfield Unit (HU) Reference Scale:</strong>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '6px', marginTop: '6px' }}>
              <div>• Fluid / Simple Cyst: -10 to +20 HU</div>
              <div>• Normal Parenchyma: +30 to +50 HU</div>
              <div>• Enhancing RCC Mass: +70 to +140 HU</div>
              <div>• Calcium / Stones: +300 to +1500 HU</div>
            </div>
          </div>
        </div>

        {/* Modal Footer */}
        <div
          style={{
            padding: '12px 20px',
            backgroundColor: '#0f172a',
            borderTop: '1px solid #1e293b',
            display: 'flex',
            justifyContent: 'flex-end'
          }}
        >
          <button
            onClick={onClose}
            style={{
              backgroundColor: '#38bdf8',
              border: 'none',
              borderRadius: '8px',
              padding: '8px 18px',
              fontSize: '12px',
              fontWeight: 700,
              color: '#041e28',
              cursor: 'pointer'
            }}
          >
            Close Guide
          </button>
        </div>
      </div>
    </div>
  );
};
