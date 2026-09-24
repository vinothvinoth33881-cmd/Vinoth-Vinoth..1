import React, { useRef, useEffect } from 'react';

export const RocCurvesCanvas: React.FC = () => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const w = canvas.width;
    const h = canvas.height;
    ctx.clearRect(0, 0, w, h);

    const padL = 40;
    const padR = 20;
    const padT = 20;
    const padB = 30;
    const plotW = w - padL - padR;
    const plotH = h - padT - padB;

    // Background grid
    ctx.strokeStyle = '#1e293b';
    ctx.lineWidth = 1;
    for (let i = 0; i <= 5; i++) {
      const y = padT + (plotH / 5) * i;
      ctx.beginPath();
      ctx.moveTo(padL, y);
      ctx.lineTo(w - padR, y);
      ctx.stroke();

      const x = padL + (plotW / 5) * i;
      ctx.beginPath();
      ctx.moveTo(x, padT);
      ctx.lineTo(x, h - padB);
      ctx.stroke();

      // Axis Labels
      ctx.fillStyle = '#64748b';
      ctx.font = '9px monospace';
      ctx.fillText((1 - i * 0.2).toFixed(1), 10, y + 3);
      ctx.fillText((i * 0.2).toFixed(1), x - 6, h - 12);
    }

    // Diagonal random chance line
    ctx.strokeStyle = '#475569';
    ctx.setLineDash([4, 4]);
    ctx.beginPath();
    ctx.moveTo(padL, h - padB);
    ctx.lineTo(w - padR, padT);
    ctx.stroke();
    ctx.setLineDash([]);

    // Helper to plot curves
    const drawCurve = (color: string, auc: string, points: [number, number][]) => {
      ctx.strokeStyle = color;
      ctx.lineWidth = 2;
      ctx.beginPath();
      points.forEach(([fpr, tpr], idx) => {
        const x = padL + fpr * plotW;
        const y = padT + (1 - tpr) * plotH;
        if (idx === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      });
      ctx.stroke();
    };

    // Plot individual class ROCs
    // Tumor (AUC 0.999)
    drawCurve('#ef4444', '0.999', [[0, 0], [0.005, 0.96], [0.01, 0.99], [0.05, 0.998], [1, 1]]);
    // Cyst (AUC 0.998)
    drawCurve('#06b6d4', '0.998', [[0, 0], [0.008, 0.95], [0.015, 0.985], [0.05, 0.996], [1, 1]]);
    // Stone (AUC 0.996)
    drawCurve('#f59e0b', '0.996', [[0, 0], [0.012, 0.93], [0.025, 0.98], [0.06, 0.994], [1, 1]]);
    // Normal (AUC 0.998)
    drawCurve('#10b981', '0.998', [[0, 0], [0.006, 0.955], [0.012, 0.988], [0.04, 0.997], [1, 1]]);
  }, []);

  return (
    <div
      style={{
        backgroundColor: '#090f1d',
        border: '1px solid #1e293b',
        borderRadius: '14px',
        padding: '16px'
      }}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
        <div>
          <div style={{ fontSize: '11.5px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px' }}>
            RECEIVER OPERATING CHARACTERISTIC (ROC) CURVES
          </div>
          <div style={{ fontSize: '10px', color: '#94a3b8' }}>
            Macro-Average Area Under Curve (AUC): 0.998
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'center', margin: '8px 0' }}>
        <canvas ref={canvasRef} width={420} height={200} style={{ maxWidth: '100%', height: 'auto' }} />
      </div>

      {/* Legend */}
      <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', justifyContent: 'center', fontSize: '10.5px' }}>
        <span style={{ color: '#ef4444', display: 'flex', alignItems: 'center', gap: '4px' }}>
          ● Tumor (AUC: 0.999)
        </span>
        <span style={{ color: '#06b6d4', display: 'flex', alignItems: 'center', gap: '4px' }}>
          ● Cyst (AUC: 0.998)
        </span>
        <span style={{ color: '#f59e0b', display: 'flex', alignItems: 'center', gap: '4px' }}>
          ● Stone (AUC: 0.996)
        </span>
        <span style={{ color: '#10b981', display: 'flex', alignItems: 'center', gap: '4px' }}>
          ● Normal (AUC: 0.998)
        </span>
      </div>
    </div>
  );
};
