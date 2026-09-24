import React, { useState } from 'react';
import { ShieldAlert, ChevronDown, ChevronUp, AlertTriangle } from 'lucide-react';

interface AcademicDisclaimerProps {
  initiallyExpanded?: boolean;
}

export const AcademicDisclaimer: React.FC<AcademicDisclaimerProps> = ({ initiallyExpanded = false }) => {
  const [isExpanded, setIsExpanded] = useState(initiallyExpanded);

  return (
    <div
      style={{
        backgroundColor: '#0f172a',
        border: '1px solid rgba(245, 158, 11, 0.4)',
        borderRadius: '12px',
        padding: '12px 16px',
        marginBottom: '16px'
      }}
    >
      <div
        onClick={() => setIsExpanded(!isExpanded)}
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          cursor: 'pointer'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div
            style={{
              width: '32px',
              height: '32px',
              borderRadius: '8px',
              backgroundColor: 'rgba(245, 158, 11, 0.15)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            <ShieldAlert size={18} color="#f59e0b" />
          </div>
          <div>
            <div style={{ fontSize: '11px', fontWeight: 700, color: '#f59e0b', letterSpacing: '0.5px' }}>
              CLINICAL DECISION SUPPORT & RESEARCH PROTOTYPE
            </div>
            <div style={{ fontSize: '12px', color: '#cbd5e1', fontWeight: 500 }}>
              Notice: For research, educational, and second-opinion decision support only.
            </div>
          </div>
        </div>

        <button
          style={{
            background: 'none',
            border: 'none',
            color: '#94a3b8',
            cursor: 'pointer',
            padding: '4px'
          }}
          aria-label="Toggle disclaimer details"
        >
          {isExpanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
        </button>
      </div>

      {isExpanded && (
        <div
          style={{
            marginTop: '12px',
            paddingTop: '10px',
            borderTop: '1px solid rgba(255, 255, 255, 0.08)',
            fontSize: '11px',
            color: '#94a3b8',
            lineHeight: 1.6
          }}
        >
          <div style={{ display: 'flex', gap: '8px', marginBottom: '8px' }}>
            <AlertTriangle size={14} color="#f59e0b" style={{ flexShrink: 0, marginTop: '2px' }} />
            <span>
              <strong>Not a substitute for clinical judgment:</strong> Predictions rendered by this deep learning engine
              (EfficientNet-B4, ResNet-50, VGG-16, Ensemble) are probabilistic estimations based on trained benchmark datasets.
            </span>
          </div>
          <p style={{ margin: '0 0 6px 0' }}>
            All imaging findings must be verified by a board-certified radiologist or licensed nephrologist in conjunction with
            complete clinical history, physical examination, laboratory markers (eGFR, Creatinine, urinalysis), and biopsy where indicated.
          </p>
          <div style={{ color: '#64748b', fontSize: '10px' }}>
            Institutional Review Board (IRB) Protocol: RES-2026-NEPHRO-AI • Compliant with HIPAA de-identification standards (45 CFR § 164.514).
          </div>
        </div>
      )}
    </div>
  );
};
