import React from 'react';
import { Award, Zap, BarChart3, Database, ShieldCheck, Cpu } from 'lucide-react';
import { ConfusionMatrix } from '../components/ConfusionMatrix';
import { RocCurvesCanvas } from '../components/RocCurvesCanvas';
import { MODELS, ModelId } from '../types';

export const BenchmarksScreen: React.FC = () => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Header */}
      <div>
        <h1 style={{ margin: 0, fontSize: '20px', fontWeight: 800, color: '#ffffff' }}>
          Deep Learning Models & Benchmarks
        </h1>
        <div style={{ fontSize: '11.5px', color: '#94a3b8' }}>
          Empirical Validation on N=12,446 Contrast-Enhanced Abdominal CT Scans
        </div>
      </div>

      {/* 4 KPI Metric Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))', gap: '10px' }}>
        <div
          style={{
            backgroundColor: '#0d1527',
            border: '1px solid #1e293b',
            borderRadius: '12px',
            padding: '12px',
            display: 'flex',
            flexDirection: 'column',
            gap: '4px'
          }}
        >
          <div style={{ fontSize: '10px', color: '#94a3b8', fontWeight: 600 }}>OVERALL ACCURACY</div>
          <div style={{ fontSize: '22px', fontWeight: 800, color: '#10b981', fontFamily: 'JetBrains Mono, monospace' }}>
            99.14%
          </div>
          <div style={{ fontSize: '9.5px', color: '#64748b' }}>Independent Test Set</div>
        </div>

        <div
          style={{
            backgroundColor: '#0d1527',
            border: '1px solid #1e293b',
            borderRadius: '12px',
            padding: '12px',
            display: 'flex',
            flexDirection: 'column',
            gap: '4px'
          }}
        >
          <div style={{ fontSize: '10px', color: '#94a3b8', fontWeight: 600 }}>MACRO F1-SCORE</div>
          <div style={{ fontSize: '22px', fontWeight: 800, color: '#38bdf8', fontFamily: 'JetBrains Mono, monospace' }}>
            99.02%
          </div>
          <div style={{ fontSize: '9.5px', color: '#64748b' }}>Harmonic Mean</div>
        </div>

        <div
          style={{
            backgroundColor: '#0d1527',
            border: '1px solid #1e293b',
            borderRadius: '12px',
            padding: '12px',
            display: 'flex',
            flexDirection: 'column',
            gap: '4px'
          }}
        >
          <div style={{ fontSize: '10px', color: '#94a3b8', fontWeight: 600 }}>SENSITIVITY (RECALL)</div>
          <div style={{ fontSize: '22px', fontWeight: 800, color: '#a855f7', fontFamily: 'JetBrains Mono, monospace' }}>
            99.18%
          </div>
          <div style={{ fontSize: '9.5px', color: '#64748b' }}>Tumor Detection: 99.5%</div>
        </div>

        <div
          style={{
            backgroundColor: '#0d1527',
            border: '1px solid #1e293b',
            borderRadius: '12px',
            padding: '12px',
            display: 'flex',
            flexDirection: 'column',
            gap: '4px'
          }}
        >
          <div style={{ fontSize: '10px', color: '#94a3b8', fontWeight: 600 }}>AUC-ROC MACRO</div>
          <div style={{ fontSize: '22px', fontWeight: 800, color: '#f59e0b', fontFamily: 'JetBrains Mono, monospace' }}>
            0.998
          </div>
          <div style={{ fontSize: '9.5px', color: '#64748b' }}>Near-Ideal Discrimination</div>
        </div>
      </div>

      {/* Multiclass Confusion Matrix */}
      <ConfusionMatrix />

      {/* ROC Curves Visualization */}
      <RocCurvesCanvas />

      {/* Architecture Comparison Table */}
      <div
        style={{
          backgroundColor: '#090f1d',
          border: '1px solid #1e293b',
          borderRadius: '14px',
          padding: '16px'
        }}
      >
        <div style={{ fontSize: '12px', fontWeight: 700, color: '#38bdf8', marginBottom: '10px' }}>
          MODEL ARCHITECTURE PERFORMANCE BENCHMARK
        </div>
        <div style={{ overflowX: 'auto' }}>
          <table
            style={{
              width: '100%',
              borderCollapse: 'collapse',
              fontSize: '11px',
              textAlign: 'left'
            }}
          >
            <thead>
              <tr style={{ color: '#94a3b8', borderBottom: '1px solid #1e293b' }}>
                <th style={{ padding: '8px' }}>Architecture</th>
                <th style={{ padding: '8px' }}>Params</th>
                <th style={{ padding: '8px' }}>Accuracy</th>
                <th style={{ padding: '8px' }}>Macro F1</th>
                <th style={{ padding: '8px' }}>Latency</th>
                <th style={{ padding: '8px' }}>Status</th>
              </tr>
            </thead>
            <tbody>
              {(Object.keys(MODELS) as ModelId[]).map((mId) => {
                const m = MODELS[mId];
                const isDefault = mId === 'EFFICIENTNET_B4';
                return (
                  <tr key={mId} style={{ borderBottom: '1px solid rgba(255,255,255,0.04)' }}>
                    <td style={{ padding: '8px', fontWeight: 700, color: '#f8fafc' }}>
                      {m.shortName}
                    </td>
                    <td style={{ padding: '8px', color: '#94a3b8', fontFamily: 'monospace' }}>{m.parameters}</td>
                    <td style={{ padding: '8px', color: '#10b981', fontWeight: 700 }}>
                      {(m.accuracy * 100).toFixed(1)}%
                    </td>
                    <td style={{ padding: '8px', color: '#38bdf8' }}>
                      {(m.f1Score * 100).toFixed(1)}%
                    </td>
                    <td style={{ padding: '8px', color: '#cbd5e1', fontFamily: 'monospace' }}>~{m.latencyMs}ms</td>
                    <td style={{ padding: '8px' }}>
                      {isDefault ? (
                        <span style={{ backgroundColor: 'rgba(56, 189, 248, 0.2)', color: '#38bdf8', padding: '2px 6px', borderRadius: '4px', fontSize: '9.5px', fontWeight: 700 }}>
                          Default Engine
                        </span>
                      ) : (
                        <span style={{ color: '#64748b', fontSize: '9.5px' }}>Verified</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Dataset Cohort Breakdown */}
      <div
        style={{
          backgroundColor: '#0d1527',
          border: '1px solid #1e293b',
          borderRadius: '14px',
          padding: '14px'
        }}
      >
        <div style={{ fontSize: '12px', fontWeight: 700, color: '#f8fafc', marginBottom: '8px' }}>
          Dataset Cohort Composition (CT Kidney Dataset)
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: '8px', fontSize: '11px' }}>
          <div style={{ backgroundColor: '#090f1d', padding: '8px', borderRadius: '8px' }}>
            <div style={{ color: '#10b981', fontWeight: 700 }}>Normal Parenchyma</div>
            <div style={{ color: '#94a3b8', marginTop: '2px' }}>3,450 CT Slices (27.7%)</div>
          </div>
          <div style={{ backgroundColor: '#090f1d', padding: '8px', borderRadius: '8px' }}>
            <div style={{ color: '#06b6d4', fontWeight: 700 }}>Simple Renal Cyst</div>
            <div style={{ color: '#94a3b8', marginTop: '2px' }}>3,709 CT Slices (29.8%)</div>
          </div>
          <div style={{ backgroundColor: '#090f1d', padding: '8px', borderRadius: '8px' }}>
            <div style={{ color: '#f59e0b', fontWeight: 700 }}>Nephrolithiasis</div>
            <div style={{ color: '#94a3b8', marginTop: '2px' }}>1,377 CT Slices (11.1%)</div>
          </div>
          <div style={{ backgroundColor: '#090f1d', padding: '8px', borderRadius: '8px' }}>
            <div style={{ color: '#ef4444', fontWeight: 700 }}>Renal Cell Carcinoma</div>
            <div style={{ color: '#94a3b8', marginTop: '2px' }}>3,910 CT Slices (31.4%)</div>
          </div>
        </div>
      </div>
    </div>
  );
};
