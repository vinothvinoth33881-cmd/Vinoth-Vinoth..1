import React, { useState } from 'react';
import {
  FileText,
  Copy,
  Check,
  CheckCircle,
  AlertTriangle,
  ArrowLeft,
  Building2,
  Stethoscope,
  ShieldCheck,
  Calendar,
  Share2
} from 'lucide-react';
import { CaseRecord, DIAGNOSIS_CLASSES } from '../types';

interface ClinicalReportScreenProps {
  record: CaseRecord;
  onUpdateRecord: (updated: CaseRecord) => void;
  onBack: () => void;
}

export const ClinicalReportScreen: React.FC<ClinicalReportScreenProps> = ({
  record,
  onUpdateRecord,
  onBack
}) => {
  const [copied, setCopied] = useState<boolean>(false);
  const [isVerified, setIsVerified] = useState<boolean>(record.reviewStatus.includes('Verified'));
  const [notes, setNotes] = useState<string>(record.clinicianNotes || '');

  const cls = DIAGNOSIS_CLASSES[record.primaryDiagnosis];
  const dateFormatted = new Date(record.timestamp).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });

  const handleToggleVerification = () => {
    const nextVerified = !isVerified;
    setIsVerified(nextVerified);
    const updated: CaseRecord = {
      ...record,
      reviewStatus: nextVerified ? 'Verified by Physician' : 'Pending Radiologist Review',
      clinicianNotes: notes
    };
    onUpdateRecord(updated);
  };

  const handleSaveNotes = () => {
    const updated: CaseRecord = {
      ...record,
      clinicianNotes: notes
    };
    onUpdateRecord(updated);
  };

  const copyReportText = () => {
    const text = `================================================================
ACADEMIC NEPHROLOGY & RADIOLOGICAL DECISION SUPPORT REPORT
================================================================
CASE ACCESSION: ACC-${record.patientId}-CT
PATIENT ID:     ${record.patientId}
DEMOGRAPHICS:   ${record.patientAge}yo ${record.patientGender}
STUDY DATE:     ${dateFormatted}
SCAN PROTOCOL:  ${record.scanType}
AI ENGINE:      ${record.activeModel} (Deep Multimodal CNN)

----------------------------------------------------------------
1. CLINICAL PRESENTATION & SYMPTOMS:
${record.symptoms}

2. MULTIMODAL RENAL BIOMARKERS:
- eGFR:               ${record.egfr} mL/min/1.73m²
- Serum Creatinine:   ${record.serumCreatinine} mg/dL
- BUN:                ${record.bun} mg/dL
- Urine Protein:      ${record.urineProtein}
- Blood Pressure:     ${record.bloodPressure} mmHg
- Functional Status:  ${record.ckdStage}

3. AI DEEP LEARNING MORPHOLOGY & FINDINGS:
- Primary Prediction: ${cls.scientificName}
- Model Confidence:   ${(record.confidence * 100).toFixed(1)}%
- Softmax Probs:      Normal: ${(record.probabilities.NORMAL * 100).toFixed(1)}% | Cyst: ${(record.probabilities.CYST * 100).toFixed(1)}% | Stone: ${(record.probabilities.STONE * 100).toFixed(1)}% | Tumor: ${(record.probabilities.TUMOR * 100).toFixed(1)}%
- Imaging Pattern:    ${cls.summary}

4. CLINICAL IMPRESSION & RISK STRATIFICATION:
- Risk Tier:          ${record.riskLevel}
- Guidance Protocol:  ${cls.clinicalGuidance}

5. CLINICIAN ATTESTATION & NOTES:
- Verification State: ${isVerified ? 'VERIFIED BY LICENSED PHYSICIAN' : 'PENDING RADIOLOGIST REVIEW'}
- Reviewer Notes:     ${notes}

================================================================
DISCLAIMER: For research/educational clinical decision-support only.
Final interpretation must be performed by a qualified physician.
================================================================`;

    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 3000);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Top action row */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <button
          onClick={onBack}
          style={{
            backgroundColor: '#1e293b',
            border: 'none',
            borderRadius: '8px',
            color: '#cbd5e1',
            padding: '8px 12px',
            fontSize: '11.5px',
            fontWeight: 600,
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            cursor: 'pointer'
          }}
        >
          <ArrowLeft size={16} />
          Back to Workstation
        </button>

        <button
          onClick={copyReportText}
          style={{
            backgroundColor: copied ? '#10b981' : '#38bdf8',
            border: 'none',
            borderRadius: '8px',
            color: '#041e28',
            padding: '8px 14px',
            fontSize: '11.5px',
            fontWeight: 700,
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            cursor: 'pointer'
          }}
        >
          {copied ? <Check size={16} /> : <Copy size={16} />}
          {copied ? 'Report Copied!' : 'Copy Formatted Report'}
        </button>
      </div>

      {/* Formal Medical Report Container */}
      <div
        style={{
          backgroundColor: '#090f1e',
          border: '1px solid #1e293b',
          borderRadius: '16px',
          padding: '24px',
          display: 'flex',
          flexDirection: 'column',
          gap: '18px',
          boxShadow: '0 10px 30px rgba(0,0,0,0.5)'
        }}
      >
        {/* Hospital / Clinic Letterhead Header */}
        <div
          style={{
            borderBottom: '2px solid #38bdf8',
            paddingBottom: '16px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'flex-start',
            flexWrap: 'wrap',
            gap: '12px'
          }}
        >
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Building2 size={20} color="#38bdf8" />
              <span style={{ fontSize: '15px', fontWeight: 800, color: '#f8fafc', letterSpacing: '0.5px' }}>
                NEPHROLOGY RADIOLOGY DECISION SUPPORT
              </span>
            </div>
            <div style={{ fontSize: '11px', color: '#94a3b8', marginTop: '2px' }}>
              Center for Artificial Intelligence in Medical Imaging • Clinical Consultation Service
            </div>
          </div>

          <div style={{ textAlign: 'right', fontSize: '11px', color: '#94a3b8' }}>
            <div style={{ fontWeight: 700, color: '#f8fafc' }}>ACCESSION: ACC-{record.patientId}-CT</div>
            <div>{dateFormatted}</div>
          </div>
        </div>

        {/* Patient Demographics Table */}
        <div
          style={{
            backgroundColor: '#0d1527',
            border: '1px solid #1e293b',
            borderRadius: '10px',
            padding: '12px 16px',
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))',
            gap: '10px',
            fontSize: '11px'
          }}
        >
          <div>
            <span style={{ color: '#64748b' }}>Patient ID: </span>
            <strong style={{ color: '#38bdf8', fontFamily: 'monospace' }}>{record.patientId}</strong>
          </div>
          <div>
            <span style={{ color: '#64748b' }}>Age / Sex: </span>
            <strong style={{ color: '#f8fafc' }}>{record.patientAge} yo / {record.patientGender}</strong>
          </div>
          <div>
            <span style={{ color: '#64748b' }}>Study: </span>
            <strong style={{ color: '#f8fafc' }}>{record.scanType}</strong>
          </div>
          <div>
            <span style={{ color: '#64748b' }}>Model: </span>
            <strong style={{ color: '#f8fafc' }}>{record.activeModel}</strong>
          </div>
        </div>

        {/* Section 1: Clinical Presentation */}
        <div>
          <div style={{ fontSize: '11.5px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px', marginBottom: '4px' }}>
            1. CLINICAL PRESENTATION & REASON FOR EXAM
          </div>
          <div style={{ fontSize: '12px', color: '#cbd5e1', lineHeight: 1.5, backgroundColor: '#0c1424', padding: '10px 14px', borderRadius: '8px' }}>
            {record.symptoms}
          </div>
        </div>

        {/* Section 2: Multimodal Laboratory Parameters */}
        <div>
          <div style={{ fontSize: '11.5px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px', marginBottom: '4px' }}>
            2. MULTIMODAL RENAL BIOMARKERS & PHYSIOLOGY
          </div>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))',
              gap: '8px',
              backgroundColor: '#0c1424',
              padding: '10px 14px',
              borderRadius: '8px',
              fontSize: '11px'
            }}
          >
            <div>eGFR: <strong style={{ color: '#f8fafc' }}>{record.egfr} mL/min</strong></div>
            <div>Creatinine: <strong style={{ color: '#f8fafc' }}>{record.serumCreatinine} mg/dL</strong></div>
            <div>BUN: <strong style={{ color: '#f8fafc' }}>{record.bun} mg/dL</strong></div>
            <div>Proteinuria: <strong style={{ color: '#f8fafc' }}>{record.urineProtein}</strong></div>
            <div>BP: <strong style={{ color: '#f8fafc' }}>{record.bloodPressure}</strong></div>
            <div style={{ gridColumn: '1 / -1', color: '#38bdf8' }}>
              Functional Status: <strong>{record.ckdStage}</strong>
            </div>
          </div>
        </div>

        {/* Section 3: Deep Learning Findings */}
        <div>
          <div style={{ fontSize: '11.5px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px', marginBottom: '4px' }}>
            3. DEEP LEARNING MORPHOLOGY & FINDINGS
          </div>
          <div
            style={{
              backgroundColor: '#0c1424',
              border: `1px solid ${cls.color}40`,
              borderRadius: '8px',
              padding: '12px 14px',
              display: 'flex',
              flexDirection: 'column',
              gap: '8px'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ fontSize: '13px', fontWeight: 700, color: cls.color }}>
                {cls.scientificName}
              </span>
              <span style={{ fontSize: '13px', fontWeight: 700, color: cls.color, fontFamily: 'monospace' }}>
                Confidence: {(record.confidence * 100).toFixed(1)}%
              </span>
            </div>

            <div style={{ fontSize: '11.5px', color: '#cbd5e1', lineHeight: 1.5 }}>
              {cls.summary}
            </div>

            <div style={{ fontSize: '10.5px', color: '#94a3b8', borderTop: '1px solid #1e293b', paddingTop: '6px' }}>
              Softmax Distribution: Normal {(record.probabilities.NORMAL * 100).toFixed(1)}% • Cyst {(record.probabilities.CYST * 100).toFixed(1)}% • Stone {(record.probabilities.STONE * 100).toFixed(1)}% • Tumor {(record.probabilities.TUMOR * 100).toFixed(1)}%
            </div>
          </div>
        </div>

        {/* Section 4: Clinical Impression & Triage */}
        <div>
          <div style={{ fontSize: '11.5px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px', marginBottom: '4px' }}>
            4. CLINICAL IMPRESSION & RISK STRATIFICATION
          </div>
          <div
            style={{
              backgroundColor: '#0c1424',
              borderRadius: '8px',
              padding: '12px 14px',
              display: 'flex',
              flexDirection: 'column',
              gap: '6px'
            }}
          >
            <div style={{ fontSize: '12px', fontWeight: 700, color: cls.color }}>
              {record.riskLevel}
            </div>
            <div style={{ fontSize: '11.5px', color: '#cbd5e1', lineHeight: 1.5 }}>
              {cls.clinicalGuidance}
            </div>
          </div>
        </div>

        {/* Section 5: Physician Attestation & Editable Notes */}
        <div>
          <div style={{ fontSize: '11.5px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px', marginBottom: '6px' }}>
            5. CLINICIAN ATTESTATION & REVIEW
          </div>

          <div
            style={{
              backgroundColor: '#0c1424',
              borderRadius: '8px',
              padding: '12px 14px',
              display: 'flex',
              flexDirection: 'column',
              gap: '10px'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Stethoscope size={18} color={isVerified ? '#10b981' : '#f59e0b'} />
                <span style={{ fontSize: '12px', fontWeight: 600, color: '#f8fafc' }}>
                  {isVerified ? 'Attested & Verified by Licensed Physician' : 'Verification Status: Pending Review'}
                </span>
              </div>

              <button
                onClick={handleToggleVerification}
                style={{
                  backgroundColor: isVerified ? 'rgba(16, 185, 129, 0.2)' : 'rgba(245, 158, 11, 0.2)',
                  border: `1px solid ${isVerified ? '#10b981' : '#f59e0b'}`,
                  borderRadius: '6px',
                  color: isVerified ? '#10b981' : '#f59e0b',
                  padding: '5px 12px',
                  fontSize: '11px',
                  fontWeight: 700,
                  cursor: 'pointer'
                }}
              >
                {isVerified ? 'Mark as Pending' : 'Sign & Attest Case'}
              </button>
            </div>

            <div>
              <label style={{ fontSize: '10.5px', color: '#94a3b8', display: 'block', marginBottom: '4px' }}>
                Clinician Observations & Addenda:
              </label>
              <textarea
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                onBlur={handleSaveNotes}
                rows={3}
                placeholder="Enter radiological notes, staging details, or surgical plan..."
                style={{
                  width: '100%',
                  backgroundColor: '#090f1e',
                  border: '1px solid #1e293b',
                  borderRadius: '8px',
                  padding: '8px',
                  color: '#f8fafc',
                  fontSize: '11.5px',
                  boxSizing: 'border-box'
                }}
              />
            </div>
          </div>
        </div>

        {/* Academic Legal Disclaimer Footer */}
        <div
          style={{
            borderTop: '1px solid #1e293b',
            paddingTop: '12px',
            fontSize: '10px',
            color: '#64748b',
            lineHeight: 1.5,
            textAlign: 'center'
          }}
        >
          ACADEMIC / RESEARCH USE NOTICE: This machine learning decision-support tool does not provide definitive medical diagnoses.
          All automated findings must be interpreted by a qualified medical professional in conjunction with comprehensive clinical correlation.
        </div>
      </div>
    </div>
  );
};
