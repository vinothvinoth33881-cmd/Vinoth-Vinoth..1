export type DiagnosisClass = 'NORMAL' | 'CYST' | 'STONE' | 'TUMOR';

export interface ClassMetadata {
  id: DiagnosisClass;
  displayName: string;
  scientificName: string;
  color: string;
  badgeBg: string;
  summary: string;
  clinicalGuidance: string;
}

export const DIAGNOSIS_CLASSES: Record<DiagnosisClass, ClassMetadata> = {
  NORMAL: {
    id: 'NORMAL',
    displayName: 'Normal Tissue',
    scientificName: 'Normal Corticomedullary Parenchyma',
    color: '#10b981',
    badgeBg: 'rgba(16, 185, 129, 0.15)',
    summary: 'Preserved corticomedullary differentiation, smooth reniform contours, no hydronephrosis or focal attenuation defects.',
    clinicalGuidance: 'Routine annual preventive surveillance. Maintain optimal blood pressure and adequate hydration.'
  },
  CYST: {
    id: 'CYST',
    displayName: 'Renal Cyst',
    scientificName: 'Simple Renal Cyst (Bosniak I/II)',
    color: '#06b6d4',
    badgeBg: 'rgba(6, 182, 212, 0.15)',
    summary: 'Homogeneous fluid-density lesion (<15 HU), paper-thin unenhancing walls, imperceptible margins with adjacent parenchyma.',
    clinicalGuidance: 'Benign Bosniak Category I/II finding. Periodic ultrasound surveillance at 12–24 month intervals.'
  },
  STONE: {
    id: 'STONE',
    displayName: 'Nephrolithiasis',
    scientificName: 'Nephrolithiasis / Calculus',
    color: '#f59e0b',
    badgeBg: 'rgba(245, 158, 11, 0.15)',
    summary: 'Well-circumscribed hyperdense focus (>300 HU) within the renal calyces/pelvis with mild upstream pelvicalyceal fullness.',
    clinicalGuidance: 'Urological consultation recommended. Hydration therapy, medical expulsive therapy or lithotripsy (ESWL/URS).'
  },
  TUMOR: {
    id: 'TUMOR',
    displayName: 'Renal Tumor (RCC)',
    scientificName: 'Renal Cell Carcinoma / Cortical Mass',
    color: '#ef4444',
    badgeBg: 'rgba(239, 68, 68, 0.15)',
    summary: 'Heterogeneously enhancing cortical mass disrupting normal reniform architecture with internal vascularity and central necrosis.',
    clinicalGuidance: 'Urgent Multidisciplinary Uro-Oncology referral. Triphasic renal protocol CT/MRI and surgical staging (partial/radical nephrectomy).'
  }
};

export type ModelId = 'EFFICIENTNET_B4' | 'RESNET_50' | 'VGG_16' | 'ENSEMBLE';

export interface ModelInfo {
  id: ModelId;
  shortName: string;
  displayName: string;
  accuracy: number;
  f1Score: number;
  sensitivity: number;
  latencyMs: number;
  parameters: string;
}

export const MODELS: Record<ModelId, ModelInfo> = {
  EFFICIENTNET_B4: {
    id: 'EFFICIENTNET_B4',
    shortName: 'EfficientNet-B4',
    displayName: 'EfficientNet-B4 (AutoAugment + Compound Scaling)',
    accuracy: 0.9914,
    f1Score: 0.9902,
    sensitivity: 0.9918,
    latencyMs: 42,
    parameters: '19.3M'
  },
  RESNET_50: {
    id: 'RESNET_50',
    shortName: 'ResNet-50',
    displayName: 'ResNet-50 (Residual Deep Learning Framework)',
    accuracy: 0.9841,
    f1Score: 0.9829,
    sensitivity: 0.9835,
    latencyMs: 28,
    parameters: '25.6M'
  },
  VGG_16: {
    id: 'VGG_16',
    shortName: 'VGG-16',
    displayName: 'VGG-16 (Simonyan & Zisserman Architecture)',
    accuracy: 0.9682,
    f1Score: 0.9654,
    sensitivity: 0.9670,
    latencyMs: 55,
    parameters: '138.4M'
  },
  ENSEMBLE: {
    id: 'ENSEMBLE',
    shortName: 'Ensemble-Voting',
    displayName: 'Deep Ensemble (ResNet-50 + EfficientNet-B4 Soft-Voting)',
    accuracy: 0.9962,
    f1Score: 0.9958,
    sensitivity: 0.9960,
    latencyMs: 85,
    parameters: '44.9M'
  }
};

export interface BenchmarkCase {
  caseId: string;
  title: string;
  groundTruth: DiagnosisClass;
  patientAge: number;
  patientGender: string;
  scanType: string;
  egfr: number;
  serumCreatinine: number;
  bun: number;
  urineProtein: string;
  systolicBp: number;
  diastolicBp: number;
  symptoms: string;
  imageUrl: string;
}

export interface PredictionResult {
  primaryClass: DiagnosisClass;
  confidence: number;
  probabilities: Record<DiagnosisClass, number>;
  modelUsed: ModelInfo;
  riskLevel: string;
  riskColor: string;
  ckdStage: string;
  latencyMs: number;
  recommendations: string[];
}

export interface CaseRecord {
  id: string;
  patientId: string;
  patientAge: number;
  patientGender: string;
  scanType: string;
  imageUrl: string;
  primaryDiagnosis: DiagnosisClass;
  confidence: number;
  probabilities: Record<DiagnosisClass, number>;
  activeModel: string;
  egfr: number;
  serumCreatinine: number;
  bun: number;
  urineProtein: string;
  bloodPressure: string;
  symptoms: string;
  riskLevel: string;
  ckdStage: string;
  reviewStatus: string;
  clinicianNotes: string;
  timestamp: number;
}
