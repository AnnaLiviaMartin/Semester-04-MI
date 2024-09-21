type NachrichtenTyp = 'TOUR' | 'ORT' | 'BENUTZER'
type Operation = 'GET' | 'CREATE' | 'UPDATE' | 'DELETE'

export interface IFrontendNachrichtEvent {
  typ: NachrichtenTyp;
  id: number;
  operation: Operation;
}