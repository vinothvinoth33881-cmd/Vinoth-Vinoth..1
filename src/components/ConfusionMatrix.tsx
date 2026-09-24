import React from 'react';

export const ConfusionMatrix: React.FC = () => {
  // Test partition values (N = 1,867 slices)
  const matrixData = [
    { actual: 'Normal', predNormal: 512, predCyst: 3, predStone: 2, predTumor: 1, total: 518, precision: '98.8%', recall: '98.8%', f1: '0.988' },
    { actual: 'Cyst', predNormal: 2, predCyst: 550, predStone: 1, predTumor: 3, total: 556, precision: '99.1%', recall: '98.9%', f1: '0.990' },
    { actual: 'Stone', predNormal: 1, predCyst: 1, predStone: 203, predTumor: 2, total: 207, precision: '98.1%', recall: '98.1%', f1: '0.981' },
    { actual: 'Tumor', predNormal: 1, predCyst: 1, predStone: 1, predTumor: 583, total: 586, precision: '99.0%', recall: '99.5%', f1: '0.992' }
  ];

  return (
    <div
      style={{
        backgroundColor: '#090f1d',
        border: '1px solid #1e293b',
        borderRadius: '14px',
        padding: '16px',
        overflowX: 'auto'
      }}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
        <div>
          <div style={{ fontSize: '11.5px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px' }}>
            4×4 MULTICLASS CONFUSION MATRIX
          </div>
          <div style={{ fontSize: '10.5px', color: '#94a3b8' }}>
            Independent Hold-out Test Partition (N = 1,867 CT Slices)
          </div>
        </div>
        <div
          style={{
            backgroundColor: 'rgba(16, 185, 129, 0.15)',
            color: '#10b981',
            padding: '3px 8px',
            borderRadius: '6px',
            fontSize: '10px',
            fontWeight: 700
          }}
        >
          Overall Accuracy: 99.14%
        </div>
      </div>

      <table
        style={{
          width: '100%',
          borderCollapse: 'collapse',
          fontSize: '11px',
          textAlign: 'center',
          fontFamily: 'JetBrains Mono, monospace'
        }}
      >
        <thead>
          <tr style={{ color: '#94a3b8', borderBottom: '1px solid #1e293b' }}>
            <th style={{ textAlign: 'left', padding: '8px' }}>Actual \ Pred</th>
            <th style={{ padding: '8px', color: '#10b981' }}>Normal</th>
            <th style={{ padding: '8px', color: '#06b6d4' }}>Cyst</th>
            <th style={{ padding: '8px', color: '#f59e0b' }}>Stone</th>
            <th style={{ padding: '8px', color: '#ef4444' }}>Tumor</th>
            <th style={{ padding: '8px', color: '#cbd5e1' }}>Precision</th>
            <th style={{ padding: '8px', color: '#cbd5e1' }}>Recall</th>
            <th style={{ padding: '8px', color: '#cbd5e1' }}>F1</th>
          </tr>
        </thead>
        <tbody>
          {matrixData.map((row, idx) => (
            <tr key={idx} style={{ borderBottom: '1px solid rgba(255,255,255,0.04)' }}>
              <td style={{ textAlign: 'left', padding: '8px', fontWeight: 700, color: '#f1f5f9' }}>
                {row.actual}
              </td>
              <td style={{ padding: '8px', backgroundColor: idx === 0 ? 'rgba(16, 185, 129, 0.25)' : 'transparent', color: idx === 0 ? '#10b981' : '#64748b', fontWeight: idx === 0 ? 700 : 400 }}>
                {row.predNormal}
              </td>
              <td style={{ padding: '8px', backgroundColor: idx === 1 ? 'rgba(6, 182, 212, 0.25)' : 'transparent', color: idx === 1 ? '#06b6d4' : '#64748b', fontWeight: idx === 1 ? 700 : 400 }}>
                {row.predCyst}
              </td>
              <td style={{ padding: '8px', backgroundColor: idx === 2 ? 'rgba(245, 158, 11, 0.25)' : 'transparent', color: idx === 2 ? '#f59e0b' : '#64748b', fontWeight: idx === 2 ? 700 : 400 }}>
                {row.predStone}
              </td>
              <td style={{ padding: '8px', backgroundColor: idx === 3 ? 'rgba(239, 68, 68, 0.25)' : 'transparent', color: idx === 3 ? '#ef4444' : '#64748b', fontWeight: idx === 3 ? 700 : 400 }}>
                {row.predTumor}
              </td>
              <td style={{ padding: '8px', color: '#38bdf8' }}>{row.precision}</td>
              <td style={{ padding: '8px', color: '#a855f7' }}>{row.recall}</td>
              <td style={{ padding: '8px', color: '#10b981', fontWeight: 700 }}>{row.f1}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
