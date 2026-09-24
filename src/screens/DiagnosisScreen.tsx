import React, { useState, useRef } from 'react';
import {
  BrainCircuit,
  Upload,
  BookmarkPlus,
  FileText,
  Activity,
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  Cpu,
  FlaskConical,
  HelpCircle
} from 'lucide-react';
import { BenchmarkCase, CaseRecord, DIAGNOSIS_CLASSES, DiagnosisClass, ModelId, MODELS, PredictionResult } from '../types';
import { BENCHMARK_CASES } from '../data/benchmarkCases';
import { InferenceEngine } from '../services/inferenceEngine';
import { DicomViewer } from '../components/DicomViewer';
import { AcademicDisclaimer } from '../components/AcademicDisclaimer';

interface DiagnosisScreenProps {
  onSaveCase: (record: CaseRecord) => void;
  onViewReport: (record: CaseRecord) => void;
  onOpenGuide: () => void;
}

export const DiagnosisScreen: React.FC<DiagnosisScreenProps> = ({
  onSaveCase,
  onViewReport,
  onOpenGuide
}) => {
  const [selectedBenchmark, setSelectedBenchmark] = useState<BenchmarkCase | null>(BENCHMARK_CASES[0]);
  const [customImage, setCustomImage] = useState<{ url: string; name: string } | null>(null);
  const [selectedModel, setSelectedModel] = useState<ModelId>('EFFICIENTNET_B4');

  // Clinical inputs
  const [patientId, setPatientId] = useState<string>(BENCHMARK_CASES[0].caseId);
  const [patientAge, setPatientAge] = useState<number>(BENCHMARK_CASES[0].patientAge);
  const [patientGender, setPatientGender] = useState<string>(BENCHMARK_CASES[0].patientGender);
  const [scanType, setScanType] = useState<string>(BENCHMARK_CASES[0].scanType);
  const [egfr, setEgfr] = useState<number>(BENCHMARK_CASES[0].egfr);
  const [creatinine, setCreatinine] = useState<number>(BENCHMARK_CASES[0].serumCreatinine);
  const [bun, setBun] = useState<number>(BENCHMARK_CASES[0].bun);
  const [urineProtein, setUrineProtein] = useState<string>(BENCHMARK_CASES[0].urineProtein);
  const [bloodPressure, setBloodPressure] = useState<string>(`${BENCHMARK_CASES[0].systolicBp}/${BENCHMARK_CASES[0].diastolicBp}`);
  const [symptoms, setSymptoms] = useState<string>(BENCHMARK_CASES[0].symptoms);

  const [showLabs, setShowLabs] = useState<boolean>(false);
  const [isAnalyzing, setIsAnalyzing] = useState<boolean>(false);
  const [prediction, setPrediction] = useState<PredictionResult | null>(null);
  const [savedSuccessMsg, setSavedSuccessMsg] = useState<string | null>(null);

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  // Select a preset benchmark case
  const handleSelectBenchmark = (bCase: BenchmarkCase) => {
    setSelectedBenchmark(bCase);
    setCustomImage(null);
    setPatientId(bCase.caseId);
    setPatientAge(bCase.patientAge);
    setPatientGender(bCase.patientGender);
    setScanType(bCase.scanType);
    setEgfr(bCase.egfr);
    setCreatinine(bCase.serumCreatinine);
    setBun(bCase.bun);
    setUrineProtein(bCase.urineProtein);
    setBloodPressure(`${bCase.systolicBp}/${bCase.diastolicBp}`);
    setSymptoms(bCase.symptoms);
    setSavedSuccessMsg(null);
    // trigger auto inference on benchmark change
    runAnalysis(bCase, null, selectedModel, bCase.egfr, bCase.serumCreatinine, bCase.bun, bCase.urineProtein, bCase.symptoms);
  };

  // Upload custom scan image
  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    setCustomImage({ url, name: file.name });
    setSelectedBenchmark(null);
    const newId = `PT-${Math.floor(10000 + Math.random() * 90000)}`;
    setPatientId(newId);
    setSavedSuccessMsg(null);
    runAnalysis(null, file.name, selectedModel, egfr, creatinine, bun, urineProtein, symptoms);
  };

  const runAnalysis = async (
    bCase = selectedBenchmark,
    cName = customImage?.name || null,
    model = selectedModel,
    e = egfr,
    cr = creatinine,
    b = bun,
    up = urineProtein,
    sym = symptoms
  ) => {
    setIsAnalyzing(true);
    setSavedSuccessMsg(null);
    try {
      const res = await InferenceEngine.analyzeScan(bCase, cName, model, e, cr, b, up, sym);
      setPrediction(res);
    } catch (err) {
      console.error(err);
    } finally {
      setIsAnalyzing(false);
    }
  };

  // Run initial diagnosis on mount
  React.useEffect(() => {
    runAnalysis();
  }, []);

  const handleSaveCurrent = () => {
    if (!prediction) return;
    const record: CaseRecord = {
      id: `case-${Date.now()}`,
      patientId,
      patientAge,
      patientGender,
      scanType,
      imageUrl: customImage?.url || selectedBenchmark?.imageUrl || '/sample_kidney_ct.jpg',
      primaryDiagnosis: prediction.primaryClass,
      confidence: prediction.confidence,
      probabilities: prediction.probabilities,
      activeModel: prediction.modelUsed.shortName,
      egfr,
      serumCreatinine: creatinine,
      bun,
      urineProtein,
      bloodPressure,
      symptoms,
      riskLevel: prediction.riskLevel,
      ckdStage: prediction.ckdStage,
      reviewStatus: 'Pending Radiologist Review',
      clinicianNotes: `Case evaluated with ${prediction.modelUsed.displayName}. Confidence: ${(prediction.confidence * 100).toFixed(1)}%.`,
      timestamp: Date.now()
    };
    onSaveCase(record);
    setSavedSuccessMsg(`Case ${patientId} saved successfully to clinical archive.`);
  };

  const handleOpenReport = () => {
    if (!prediction) return;
    const record: CaseRecord = {
      id: `case-${Date.now()}`,
      patientId,
      patientAge,
      patientGender,
      scanType,
      imageUrl: customImage?.url || selectedBenchmark?.imageUrl || '/sample_kidney_ct.jpg',
      primaryDiagnosis: prediction.primaryClass,
      confidence: prediction.confidence,
      probabilities: prediction.probabilities,
      activeModel: prediction.modelUsed.shortName,
      egfr,
      serumCreatinine: creatinine,
      bun,
      urineProtein,
      bloodPressure,
      symptoms,
      riskLevel: prediction.riskLevel,
      ckdStage: prediction.ckdStage,
      reviewStatus: 'Pending Radiologist Review',
      clinicianNotes: `Case evaluated with ${prediction.modelUsed.displayName}. Confidence: ${(prediction.confidence * 100).toFixed(1)}%.`,
      timestamp: Date.now()
    };
    onViewReport(record);
  };

  const currentImageUrl = customImage?.url || selectedBenchmark?.imageUrl || '/sample_kidney_ct.jpg';

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Top Bar with Title and Atlas Guide Button */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ margin: 0, fontSize: '20px', fontWeight: 800, color: '#ffffff', letterSpacing: '-0.5px' }}>
            NeuroScan AI Workstation
          </h1>
          <div style={{ fontSize: '11.5px', color: '#38bdf8', fontWeight: 500 }}>
            Medical Image Diagnostic System • Multiclass Renal Pathology Deep Learning
          </div>
        </div>

        <button
          onClick={onOpenGuide}
          style={{
            backgroundColor: '#0f172a',
            border: '1px solid #334155',
            borderRadius: '8px',
            color: '#38bdf8',
            padding: '7px 12px',
            fontSize: '11px',
            fontWeight: 600,
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            cursor: 'pointer'
          }}
        >
          <HelpCircle size={15} />
          Diagnostic Atlas
        </button>
      </div>

      {/* Hero Banner with Medical Graphic */}
      <div
        style={{
          position: 'relative',
          height: '110px',
          borderRadius: '14px',
          overflow: 'hidden',
          border: '1px solid #1e293b'
        }}
      >
        <img
          src="/kidney_diag_banner.jpg"
          alt="Radiology AI Header"
          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
        />
        <div
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            background: 'linear-gradient(90deg, rgba(4, 9, 20, 0.95) 0%, rgba(4, 9, 20, 0.6) 100%)',
            display: 'flex',
            alignItems: 'center',
            padding: '0 20px'
          }}
        >
          <div>
            <div style={{ fontSize: '10px', fontWeight: 700, color: '#38bdf8', letterSpacing: '1px' }}>
              ACADEMIC DEEP LEARNING BENCHMARK SYSTEM
            </div>
            <div style={{ fontSize: '15px', fontWeight: 700, color: '#ffffff', margin: '3px 0' }}>
              Multimodal Kidney Lesion Classification (Normal, Cyst, Stone, Tumor)
            </div>
            <div style={{ fontSize: '11px', color: '#94a3b8' }}>
              Convolutional Neural Networks with Grad-CAM Attention Saliency & Clinical Staging
            </div>
          </div>
        </div>
      </div>

      {/* Mandatory Academic Disclaimer Banner */}
      <AcademicDisclaimer />

      {/* 1. Benchmark Cases & Scan Selection */}
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
          <div style={{ fontSize: '11px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px' }}>
            1. SELECT CT SCAN DATASET OR UPLOAD
          </div>

          <div>
            <input
              type="file"
              ref={fileInputRef}
              accept="image/*"
              style={{ display: 'none' }}
              onChange={handleFileUpload}
            />
            <button
              onClick={() => fileInputRef.current?.click()}
              style={{
                backgroundColor: '#1e293b',
                border: '1px solid #38bdf8',
                borderRadius: '6px',
                color: '#38bdf8',
                padding: '4px 10px',
                fontSize: '11px',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '5px',
                cursor: 'pointer'
              }}
            >
              <Upload size={13} />
              Upload Scan
            </button>
          </div>
        </div>

        {/* Horizontal Benchmark Cards Carousel */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
            gap: '8px'
          }}
        >
          {BENCHMARK_CASES.map((bCase) => {
            const isSelected = selectedBenchmark?.caseId === bCase.caseId && !customImage;
            const cls = DIAGNOSIS_CLASSES[bCase.groundTruth];
            return (
              <div
                key={bCase.caseId}
                onClick={() => handleSelectBenchmark(bCase)}
                style={{
                  backgroundColor: isSelected ? '#0a2239' : '#0d1527',
                  border: `1.5px solid ${isSelected ? '#38bdf8' : '#1e293b'}`,
                  borderRadius: '10px',
                  padding: '10px',
                  cursor: 'pointer',
                  transition: 'all 0.15s ease'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '10px', fontFamily: 'monospace', fontWeight: 700, color: '#38bdf8' }}>
                    {bCase.caseId}
                  </span>
                  <span
                    style={{
                      fontSize: '9px',
                      fontWeight: 700,
                      color: cls.color,
                      backgroundColor: cls.badgeBg,
                      padding: '2px 6px',
                      borderRadius: '4px'
                    }}
                  >
                    {cls.displayName}
                  </span>
                </div>

                <div
                  style={{
                    fontSize: '11px',
                    fontWeight: 600,
                    color: '#f8fafc',
                    margin: '6px 0 4px 0',
                    lineHeight: 1.3,
                    height: '28px',
                    overflow: 'hidden'
                  }}
                >
                  {bCase.title}
                </div>

                <div style={{ fontSize: '9.5px', color: '#94a3b8' }}>
                  {bCase.patientAge}y {bCase.patientGender} • eGFR {bCase.egfr.toFixed(0)}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* 2. Interactive DICOM Viewer */}
      <div>
        <div style={{ fontSize: '11px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px', marginBottom: '8px' }}>
          2. INTERACTIVE DICOM VIEWER & GRAD-CAM
        </div>
        <DicomViewer
          imageUrl={currentImageUrl}
          patientId={patientId}
          scanType={scanType}
          primaryClass={prediction?.primaryClass}
          confidence={prediction?.confidence}
        />
      </div>

      {/* 3. Model Architecture Selection */}
      <div>
        <div style={{ fontSize: '11px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px', marginBottom: '8px' }}>
          3. DEEP LEARNING MODEL ARCHITECTURE
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: '8px' }}>
          {(Object.keys(MODELS) as ModelId[]).map((mId) => {
            const m = MODELS[mId];
            const isSelected = selectedModel === mId;
            return (
              <button
                key={mId}
                onClick={() => {
                  setSelectedModel(mId);
                  runAnalysis(selectedBenchmark, customImage?.name || null, mId);
                }}
                style={{
                  backgroundColor: isSelected ? 'rgba(56, 189, 248, 0.15)' : '#0d1527',
                  border: `1.5px solid ${isSelected ? '#38bdf8' : '#1e293b'}`,
                  borderRadius: '10px',
                  padding: '10px',
                  textAlign: 'left',
                  cursor: 'pointer'
                }}
              >
                <div style={{ fontSize: '12px', fontWeight: 700, color: isSelected ? '#38bdf8' : '#f8fafc' }}>
                  {m.shortName}
                </div>
                <div style={{ fontSize: '9.5px', fontFamily: 'monospace', color: '#94a3b8', marginTop: '2px' }}>
                  Acc: {(m.accuracy * 100).toFixed(1)}% | ~{m.latencyMs}ms
                </div>
              </button>
            );
          })}
        </div>
      </div>

      {/* 4. Multimodal Laboratory Inputs (Collapsible) */}
      <div
        style={{
          backgroundColor: '#0d1527',
          border: '1px solid #1e293b',
          borderRadius: '12px',
          overflow: 'hidden'
        }}
      >
        <div
          onClick={() => setShowLabs(!showLabs)}
          style={{
            padding: '12px 14px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            cursor: 'pointer'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <FlaskConical size={16} color="#38bdf8" />
            <div>
              <div style={{ fontSize: '12px', fontWeight: 700, color: '#f8fafc' }}>
                Multimodal Lab & Clinical Profile
              </div>
              <div style={{ fontSize: '10.5px', color: '#94a3b8' }}>
                eGFR: {egfr} mL/min • Cr: {creatinine} mg/dL • BUN: {bun} • BP: {bloodPressure}
              </div>
            </div>
          </div>
          {showLabs ? <ChevronUp size={18} color="#94a3b8" /> : <ChevronDown size={18} color="#94a3b8" />}
        </div>

        {showLabs && (
          <div
            style={{
              padding: '14px',
              borderTop: '1px solid #1e293b',
              backgroundColor: '#090f1d',
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))',
              gap: '10px'
            }}
          >
            <div>
              <label style={{ fontSize: '10px', color: '#94a3b8', display: 'block', marginBottom: '3px' }}>
                eGFR (mL/min/1.73m²)
              </label>
              <input
                type="number"
                value={egfr}
                onChange={(e) => setEgfr(parseFloat(e.target.value) || 0)}
                style={{
                  width: '100%',
                  backgroundColor: '#1e293b',
                  border: '1px solid #334155',
                  borderRadius: '6px',
                  padding: '6px',
                  color: '#fff',
                  fontSize: '11px',
                  boxSizing: 'border-box'
                }}
              />
            </div>

            <div>
              <label style={{ fontSize: '10px', color: '#94a3b8', display: 'block', marginBottom: '3px' }}>
                Serum Creatinine (mg/dL)
              </label>
              <input
                type="number"
                step="0.01"
                value={creatinine}
                onChange={(e) => setCreatinine(parseFloat(e.target.value) || 0)}
                style={{
                  width: '100%',
                  backgroundColor: '#1e293b',
                  border: '1px solid #334155',
                  borderRadius: '6px',
                  padding: '6px',
                  color: '#fff',
                  fontSize: '11px',
                  boxSizing: 'border-box'
                }}
              />
            </div>

            <div>
              <label style={{ fontSize: '10px', color: '#94a3b8', display: 'block', marginBottom: '3px' }}>
                BUN (mg/dL)
              </label>
              <input
                type="number"
                value={bun}
                onChange={(e) => setBun(parseFloat(e.target.value) || 0)}
                style={{
                  width: '100%',
                  backgroundColor: '#1e293b',
                  border: '1px solid #334155',
                  borderRadius: '6px',
                  padding: '6px',
                  color: '#fff',
                  fontSize: '11px',
                  boxSizing: 'border-box'
                }}
              />
            </div>

            <div>
              <label style={{ fontSize: '10px', color: '#94a3b8', display: 'block', marginBottom: '3px' }}>
                Blood Pressure
              </label>
              <input
                type="text"
                value={bloodPressure}
                onChange={(e) => setBloodPressure(e.target.value)}
                style={{
                  width: '100%',
                  backgroundColor: '#1e293b',
                  border: '1px solid #334155',
                  borderRadius: '6px',
                  padding: '6px',
                  color: '#fff',
                  fontSize: '11px',
                  boxSizing: 'border-box'
                }}
              />
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label style={{ fontSize: '10px', color: '#94a3b8', display: 'block', marginBottom: '3px' }}>
                Presenting Symptoms
              </label>
              <input
                type="text"
                value={symptoms}
                onChange={(e) => setSymptoms(e.target.value)}
                style={{
                  width: '100%',
                  backgroundColor: '#1e293b',
                  border: '1px solid #334155',
                  borderRadius: '6px',
                  padding: '6px',
                  color: '#fff',
                  fontSize: '11px',
                  boxSizing: 'border-box'
                }}
              />
            </div>
          </div>
        )}
      </div>

      {/* Main Action: RUN AI INFERENCE Button */}
      <button
        onClick={() => runAnalysis()}
        disabled={isAnalyzing}
        style={{
          backgroundColor: '#38bdf8',
          border: 'none',
          borderRadius: '12px',
          padding: '14px',
          color: '#041e28',
          fontSize: '13px',
          fontWeight: 800,
          letterSpacing: '0.5px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '8px',
          cursor: isAnalyzing ? 'not-allowed' : 'pointer',
          opacity: isAnalyzing ? 0.7 : 1,
          boxShadow: '0 4px 20px rgba(56, 189, 248, 0.3)'
        }}
      >
        <BrainCircuit size={18} />
        {isAnalyzing ? 'RUNNING DEEP NEURAL NETWORK INFERENCE...' : 'RUN AI DIAGNOSTIC INFERENCE'}
      </button>

      {/* Success Notification */}
      {savedSuccessMsg && (
        <div
          style={{
            backgroundColor: '#064e3b',
            border: '1px solid #10b981',
            borderRadius: '10px',
            padding: '10px 14px',
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            fontSize: '11.5px',
            color: '#ecfdf5',
            fontWeight: 500
          }}
        >
          <CheckCircle2 size={16} color="#10b981" />
          {savedSuccessMsg}
        </div>
      )}

      {/* Live Diagnostic Results Card */}
      {prediction && (
        <div
          style={{
            backgroundColor: '#091223',
            border: `1.5px solid ${prediction.riskColor}`,
            borderRadius: '16px',
            padding: '16px',
            display: 'flex',
            flexDirection: 'column',
            gap: '12px'
          }}
        >
          {/* Card Top: Primary Classification & Confidence */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <div style={{ fontSize: '10px', fontWeight: 700, color: '#94a3b8', letterSpacing: '1px' }}>
                PRIMARY AI PREDICTIVE CLASSIFICATION
              </div>
              <div
                style={{
                  fontSize: '18px',
                  fontWeight: 800,
                  color: DIAGNOSIS_CLASSES[prediction.primaryClass].color,
                  marginTop: '2px'
                }}
              >
                {DIAGNOSIS_CLASSES[prediction.primaryClass].scientificName}
              </div>
            </div>

            <div style={{ textAlign: 'right' }}>
              <div
                style={{
                  fontSize: '22px',
                  fontFamily: 'JetBrains Mono, monospace',
                  fontWeight: 800,
                  color: DIAGNOSIS_CLASSES[prediction.primaryClass].color
                }}
              >
                {(prediction.confidence * 100).toFixed(1)}%
              </div>
              <div style={{ fontSize: '10px', color: '#94a3b8' }}>Softmax Confidence</div>
            </div>
          </div>

          {/* Multimodal Risk Level Alert Badge */}
          <div
            style={{
              backgroundColor: `${prediction.riskColor}20`,
              border: `1px solid ${prediction.riskColor}60`,
              borderRadius: '8px',
              padding: '8px 12px',
              fontSize: '11.5px',
              fontWeight: 700,
              color: prediction.riskColor
            }}
          >
            {prediction.riskLevel}
          </div>

          {/* Probability Distribution for All 4 Classes */}
          <div>
            <div style={{ fontSize: '10px', fontWeight: 700, color: '#94a3b8', marginBottom: '8px' }}>
              SOFTMAX CLASS PROBABILITIES ({prediction.modelUsed.shortName}):
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {(Object.keys(prediction.probabilities) as DiagnosisClass[]).map((clsKey) => {
                const prob = prediction.probabilities[clsKey];
                const cls = DIAGNOSIS_CLASSES[clsKey];
                const isWinner = clsKey === prediction.primaryClass;
                return (
                  <div key={clsKey}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', marginBottom: '3px' }}>
                      <span style={{ fontWeight: isWinner ? 700 : 500, color: isWinner ? '#f8fafc' : '#94a3b8' }}>
                        {cls.displayName}
                      </span>
                      <span style={{ fontFamily: 'monospace', fontWeight: 700, color: cls.color }}>
                        {(prob * 100).toFixed(1)}%
                      </span>
                    </div>
                    <div
                      style={{
                        height: '6px',
                        backgroundColor: '#1e293b',
                        borderRadius: '3px',
                        overflow: 'hidden'
                      }}
                    >
                      <div
                        style={{
                          height: '100%',
                          width: `${(prob * 100).toFixed(1)}%`,
                          backgroundColor: cls.color,
                          borderRadius: '3px',
                          transition: 'width 0.4s ease'
                        }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Renal Function & Staging */}
          <div style={{ fontSize: '11px', color: '#cbd5e1', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Activity size={14} color="#38bdf8" />
            <span>Renal Function Correlation: <strong>{prediction.ckdStage}</strong></span>
          </div>

          {/* Actions: Save Record & View Full Report */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginTop: '4px' }}>
            <button
              onClick={handleSaveCurrent}
              style={{
                backgroundColor: 'transparent',
                border: '1px solid #38bdf8',
                borderRadius: '10px',
                color: '#38bdf8',
                padding: '10px',
                fontSize: '11.5px',
                fontWeight: 700,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '6px',
                cursor: 'pointer'
              }}
            >
              <BookmarkPlus size={16} />
              Save Record
            </button>

            <button
              onClick={handleOpenReport}
              style={{
                backgroundColor: '#38bdf8',
                border: 'none',
                borderRadius: '10px',
                color: '#041e28',
                padding: '10px',
                fontSize: '11.5px',
                fontWeight: 700,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '6px',
                cursor: 'pointer'
              }}
            >
              <FileText size={16} />
              Clinical Report
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
