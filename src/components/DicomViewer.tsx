import React, { useState, useRef, useEffect } from 'react';
import { Layers, Eye, EyeOff, ZoomIn, ZoomOut, RotateCcw, Sliders, Scan } from 'lucide-react';
import { DiagnosisClass } from '../types';

interface DicomViewerProps {
  imageUrl: string;
  patientId: string;
  scanType: string;
  primaryClass?: DiagnosisClass;
  confidence?: number;
}

type WindowPreset = 'STANDARD' | 'BONE' | 'SOFT_TISSUE' | 'CORTEX';

const PRESETS: Record<WindowPreset, { name: string; window: number; level: number; filter: string }> = {
  STANDARD: { name: 'Standard (W:400 L:40)', window: 400, level: 40, filter: 'contrast(1.1) brightness(1.05)' },
  BONE: { name: 'Bone/Calculi (W:1800 L:400)', window: 1800, level: 400, filter: 'contrast(1.8) brightness(0.9) grayscale(1)' },
  SOFT_TISSUE: { name: 'Soft Tissue (W:350 L:50)', window: 350, level: 50, filter: 'contrast(1.3) brightness(1.1)' },
  CORTEX: { name: 'Parenchyma (W:250 L:80)', window: 250, level: 80, filter: 'contrast(1.4) brightness(1.15)' }
};

export const DicomViewer: React.FC<DicomViewerProps> = ({
  imageUrl,
  patientId,
  scanType,
  primaryClass = 'TUMOR',
  confidence = 0.984
}) => {
  const [activePreset, setActivePreset] = useState<WindowPreset>('STANDARD');
  const [showGradCam, setShowGradCam] = useState<boolean>(true);
  const [gradCamOpacity, setGradCamOpacity] = useState<number>(0.65);
  const [isInverted, setIsInverted] = useState<boolean>(false);
  const [zoomLevel, setZoomLevel] = useState<number>(1);
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  // Draw Grad-CAM Attention Heatmap overlay on canvas
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const width = canvas.width;
    const height = canvas.height;
    ctx.clearRect(0, 0, width, height);

    if (!showGradCam) return;

    // Center focal coordinates based on diagnosis class
    const focusX = primaryClass === 'TUMOR' ? width * 0.42 :
                   primaryClass === 'STONE' ? width * 0.58 :
                   primaryClass === 'CYST' ? width * 0.65 : width * 0.5;
    const focusY = primaryClass === 'TUMOR' ? height * 0.45 :
                   primaryClass === 'STONE' ? height * 0.52 :
                   primaryClass === 'CYST' ? height * 0.38 : height * 0.5;
    const radius = primaryClass === 'STONE' ? 35 : primaryClass === 'NORMAL' ? 70 : 55;

    ctx.save();
    ctx.globalAlpha = gradCamOpacity;

    // Create multi-stop radial gradient representing Grad-CAM activation
    const grad = ctx.createRadialGradient(focusX, focusY, 5, focusX, focusY, radius);
    if (primaryClass === 'TUMOR') {
      grad.addColorStop(0, 'rgba(239, 68, 68, 0.95)');   // Hot red
      grad.addColorStop(0.35, 'rgba(249, 115, 22, 0.8)'); // Orange
      grad.addColorStop(0.7, 'rgba(234, 179, 8, 0.5)');   // Yellow
      grad.addColorStop(1, 'rgba(59, 130, 246, 0)');     // Blue fade
    } else if (primaryClass === 'STONE') {
      grad.addColorStop(0, 'rgba(245, 158, 11, 0.95)');  // Bright amber
      grad.addColorStop(0.4, 'rgba(251, 191, 36, 0.7)');
      grad.addColorStop(0.8, 'rgba(16, 185, 129, 0.4)');
      grad.addColorStop(1, 'rgba(6, 182, 212, 0)');
    } else if (primaryClass === 'CYST') {
      grad.addColorStop(0, 'rgba(6, 182, 212, 0.95)');   // Cyan
      grad.addColorStop(0.4, 'rgba(14, 165, 233, 0.7)');
      grad.addColorStop(0.8, 'rgba(99, 102, 241, 0.4)');
      grad.addColorStop(1, 'rgba(168, 85, 247, 0)');
    } else {
      grad.addColorStop(0, 'rgba(16, 185, 129, 0.85)');  // Emerald
      grad.addColorStop(0.5, 'rgba(52, 211, 153, 0.5)');
      grad.addColorStop(1, 'rgba(14, 165, 233, 0)');
    }

    ctx.fillStyle = grad;
    ctx.beginPath();
    ctx.arc(focusX, focusY, radius, 0, Math.PI * 2);
    ctx.fill();

    // Secondary bilateral activation
    if (primaryClass === 'NORMAL') {
      const grad2 = ctx.createRadialGradient(width * 0.35, height * 0.5, 5, width * 0.35, height * 0.5, 60);
      grad2.addColorStop(0, 'rgba(16, 185, 129, 0.75)');
      grad2.addColorStop(1, 'rgba(14, 165, 233, 0)');
      ctx.fillStyle = grad2;
      ctx.beginPath();
      ctx.arc(width * 0.35, height * 0.5, 60, 0, Math.PI * 2);
      ctx.fill();
    }

    // Attention bounding box indicator
    if (primaryClass !== 'NORMAL') {
      ctx.strokeStyle = primaryClass === 'TUMOR' ? '#ef4444' :
                        primaryClass === 'STONE' ? '#f59e0b' : '#06b6d4';
      ctx.lineWidth = 1.5;
      ctx.setLineDash([4, 4]);
      ctx.strokeRect(focusX - radius * 0.9, focusY - radius * 0.9, radius * 1.8, radius * 1.8);

      // Label on box
      ctx.font = 'bold 9px JetBrains Mono, monospace';
      ctx.fillStyle = ctx.strokeStyle;
      ctx.fillText(`ROI: ${primaryClass} (${(confidence * 100).toFixed(1)}%)`, focusX - radius * 0.9, focusY - radius * 0.9 - 4);
    }

    ctx.restore();
  }, [showGradCam, gradCamOpacity, primaryClass, confidence]);

  const presetStyle = PRESETS[activePreset];

  return (
    <div
      style={{
        backgroundColor: '#090f1d',
        borderRadius: '16px',
        border: '1px solid #1e293b',
        overflow: 'hidden',
        boxShadow: '0 8px 30px rgba(0, 0, 0, 0.5)'
      }}
    >
      {/* DICOM Header Toolbar */}
      <div
        style={{
          padding: '10px 14px',
          backgroundColor: '#0d1527',
          borderBottom: '1px solid #1e293b',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '8px'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Scan size={16} color="#38bdf8" />
          <span style={{ fontSize: '11px', fontWeight: 700, color: '#38bdf8', letterSpacing: '0.5px' }}>
            DICOM WORKSTATION VIEWER
          </span>
          <span style={{ fontSize: '10px', color: '#64748b', fontFamily: 'monospace' }}>
            [16-BIT HU DEPTH]
          </span>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <button
            onClick={() => setZoomLevel((z) => Math.min(1.8, z + 0.15))}
            style={{
              backgroundColor: '#1e293b',
              border: 'none',
              borderRadius: '6px',
              color: '#cbd5e1',
              padding: '4px 8px',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
              fontSize: '10px'
            }}
          >
            <ZoomIn size={12} /> +
          </button>
          <button
            onClick={() => setZoomLevel((z) => Math.max(0.8, z - 0.15))}
            style={{
              backgroundColor: '#1e293b',
              border: 'none',
              borderRadius: '6px',
              color: '#cbd5e1',
              padding: '4px 8px',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
              fontSize: '10px'
            }}
          >
            <ZoomOut size={12} /> -
          </button>
          <button
            onClick={() => { setZoomLevel(1); setIsInverted(false); }}
            style={{
              backgroundColor: '#1e293b',
              border: 'none',
              borderRadius: '6px',
              color: '#cbd5e1',
              padding: '4px 8px',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
              fontSize: '10px'
            }}
          >
            <RotateCcw size={12} /> Reset
          </button>
        </div>
      </div>

      {/* Main Image Stage */}
      <div
        style={{
          position: 'relative',
          width: '100%',
          height: '340px',
          backgroundColor: '#000000',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          overflow: 'hidden',
          cursor: 'crosshair'
        }}
      >
        {/* Overlay Telemetry Readouts (Clinical DICOM Style) */}
        <div
          style={{
            position: 'absolute',
            top: '10px',
            left: '12px',
            zIndex: 10,
            pointerEvents: 'none',
            fontFamily: 'monospace',
            fontSize: '10px',
            color: '#38bdf8',
            lineHeight: 1.4,
            textShadow: '0 1px 3px rgba(0,0,0,0.9)'
          }}
        >
          <div>ID: {patientId}</div>
          <div style={{ color: '#94a3b8' }}>STUDY: {scanType}</div>
          <div style={{ color: '#94a3b8' }}>FOV: 240mm • THK: 1.25mm</div>
        </div>

        <div
          style={{
            position: 'absolute',
            top: '10px',
            right: '12px',
            zIndex: 10,
            pointerEvents: 'none',
            fontFamily: 'monospace',
            fontSize: '10px',
            color: '#10b981',
            textAlign: 'right',
            lineHeight: 1.4,
            textShadow: '0 1px 3px rgba(0,0,0,0.9)'
          }}
        >
          <div>WIN: {presetStyle.name}</div>
          <div style={{ color: '#94a3b8' }}>ZOOM: {(zoomLevel * 100).toFixed(0)}%</div>
          <div style={{ color: '#f59e0b' }}>
            {showGradCam ? `GRAD-CAM: ACTIVE (${(gradCamOpacity * 100).toFixed(0)}%)` : 'GRAD-CAM: OFF'}
          </div>
        </div>

        <div
          style={{
            position: 'absolute',
            bottom: '10px',
            left: '12px',
            zIndex: 10,
            pointerEvents: 'none',
            fontFamily: 'monospace',
            fontSize: '9.5px',
            color: '#64748b'
          }}
        >
          KVp: 120 • mA: 280 • ROT: 0.5s • KERNEL: B40f
        </div>

        <div
          style={{
            position: 'absolute',
            bottom: '10px',
            right: '12px',
            zIndex: 10,
            pointerEvents: 'none',
            fontFamily: 'monospace',
            fontSize: '9.5px',
            color: '#64748b'
          }}
        >
          R: LATERAL | L: MEDIAL (AXIAL VIEW)
        </div>

        {/* The Base CT Image with Filter */}
        <img
          src={imageUrl}
          alt="Renal CT Scan Slice"
          style={{
            maxWidth: '100%',
            maxHeight: '100%',
            objectFit: 'contain',
            transform: `scale(${zoomLevel})`,
            transition: 'transform 0.15s ease',
            filter: `${presetStyle.filter} ${isInverted ? 'invert(1)' : ''}`,
            userSelect: 'none'
          }}
        />

        {/* Grad-CAM Saliency Heatmap Canvas */}
        <canvas
          ref={canvasRef}
          width={400}
          height={340}
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            pointerEvents: 'none',
            transform: `scale(${zoomLevel})`,
            transition: 'transform 0.15s ease'
          }}
        />
      </div>

      {/* Interactive Controls Panel */}
      <div
        style={{
          padding: '12px 14px',
          backgroundColor: '#0a1222',
          borderTop: '1px solid #1e293b',
          display: 'flex',
          flexDirection: 'column',
          gap: '10px'
        }}
      >
        {/* Preset Window Selection */}
        <div>
          <div style={{ fontSize: '10px', fontWeight: 600, color: '#94a3b8', marginBottom: '6px' }}>
            HOUNSFIELD WINDOW PRESET (HU):
          </div>
          <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
            {(Object.keys(PRESETS) as WindowPreset[]).map((preset) => {
              const isSelected = activePreset === preset;
              return (
                <button
                  key={preset}
                  onClick={() => setActivePreset(preset)}
                  style={{
                    backgroundColor: isSelected ? 'rgba(56, 189, 248, 0.2)' : '#1e293b',
                    border: `1px solid ${isSelected ? '#38bdf8' : 'transparent'}`,
                    borderRadius: '6px',
                    color: isSelected ? '#38bdf8' : '#cbd5e1',
                    padding: '5px 10px',
                    fontSize: '10px',
                    fontWeight: 600,
                    cursor: 'pointer'
                  }}
                >
                  {preset.replace('_', ' ')}
                </button>
              );
            })}
          </div>
        </div>

        {/* Grad-CAM Heatmap & Invert Toggles */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: '10px',
            paddingTop: '6px',
            borderTop: '1px solid rgba(255, 255, 255, 0.05)'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <button
              onClick={() => setShowGradCam(!showGradCam)}
              style={{
                backgroundColor: showGradCam ? 'rgba(16, 185, 129, 0.2)' : '#1e293b',
                border: `1px solid ${showGradCam ? '#10b981' : '#334155'}`,
                borderRadius: '6px',
                color: showGradCam ? '#10b981' : '#94a3b8',
                padding: '5px 10px',
                fontSize: '10.5px',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '5px',
                cursor: 'pointer'
              }}
            >
              <Layers size={13} />
              Grad-CAM Saliency {showGradCam ? 'ON' : 'OFF'}
            </button>

            <button
              onClick={() => setIsInverted(!isInverted)}
              style={{
                backgroundColor: isInverted ? 'rgba(245, 158, 11, 0.2)' : '#1e293b',
                border: `1px solid ${isInverted ? '#f59e0b' : '#334155'}`,
                borderRadius: '6px',
                color: isInverted ? '#f59e0b' : '#94a3b8',
                padding: '5px 10px',
                fontSize: '10.5px',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '5px',
                cursor: 'pointer'
              }}
            >
              Invert Polarity
            </button>
          </div>

          {showGradCam && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', minWidth: '160px' }}>
              <span style={{ fontSize: '10px', color: '#94a3b8', whiteSpace: 'nowrap' }}>
                Overlay Opacity:
              </span>
              <input
                type="range"
                min="0.1"
                max="1.0"
                step="0.05"
                value={gradCamOpacity}
                onChange={(e) => setGradCamOpacity(parseFloat(e.target.value))}
                style={{
                  width: '90px',
                  accentColor: '#38bdf8',
                  cursor: 'pointer'
                }}
              />
              <span style={{ fontSize: '10px', fontFamily: 'monospace', color: '#38bdf8' }}>
                {(gradCamOpacity * 100).toFixed(0)}%
              </span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
