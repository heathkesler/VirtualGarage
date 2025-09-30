import React from 'react';
import { AlertTriangle, X } from 'lucide-react';

const ConfirmModal = ({ 
  isOpen, 
  onClose, 
  onConfirm, 
  title = 'Confirm Action',
  message = 'Are you sure you want to proceed?',
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  confirmVariant = 'danger', // 'danger', 'primary', 'warning'
  loading = false
}) => {
  if (!isOpen) return null;

  const getConfirmButtonClasses = () => {
    const base = 'px-4 py-2 rounded-lg font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2';
    switch (confirmVariant) {
      case 'danger':
        return `${base} bg-red-600 text-white hover:bg-red-700`;
      case 'warning':
        return `${base} bg-yellow-600 text-white hover:bg-yellow-700`;
      case 'primary':
      default:
        return `${base} bg-primary-600 text-white hover:bg-primary-700`;
    }
  };

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div 
      className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4"
      onClick={handleBackdropClick}
    >
      <div className="bg-slate-800 rounded-xl max-w-md w-full border border-slate-600 shadow-2xl">
        <div className="flex items-center justify-between p-6 border-b border-slate-700">
          <div className="flex items-center gap-3">
            {confirmVariant === 'danger' && (
              <div className="p-2 bg-red-500/20 rounded-full">
                <AlertTriangle className="w-5 h-5 text-red-400" />
              </div>
            )}
            <h2 className="text-lg font-semibold text-white">
              {title}
            </h2>
          </div>
          <button
            onClick={onClose}
            className="p-1 text-slate-400 hover:text-white hover:bg-slate-700 rounded-lg transition-colors"
            disabled={loading}
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6">
          <p className="text-slate-300 leading-relaxed">
            {message}
          </p>
        </div>

        <div className="flex justify-end gap-3 p-6 border-t border-slate-700">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-slate-400 hover:text-white hover:bg-slate-700 rounded-lg transition-colors"
            disabled={loading}
          >
            {cancelText}
          </button>
          <button
            type="button"
            onClick={onConfirm}
            disabled={loading}
            className={getConfirmButtonClasses()}
          >
            {loading && (
              <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
            )}
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;