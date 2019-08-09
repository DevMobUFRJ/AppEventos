import gspread
from pprint import pprint
from oauth2client.service_account import ServiceAccountCredentials
from datetime import datetime
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore


#configurando a API do google sheets
spreadsheetID = '1uMZiPnAk8MpD6phijjFmmXkB1oVrcXXSxEHtzAN5TXk'
scope = ["https://spreadsheets.google.com/feeds",'https://www.googleapis.com/auth/spreadsheets',"https://www.googleapis.com/auth/drive.file","https://www.googleapis.com/auth/drive"]

print('Getting credentials... ', end='')
sheetsCreds = ServiceAccountCredentials.from_json_keyfile_name('creds.json', scope)
print('OK')

print('Authorizing... ', end='')
gc = gspread.authorize(sheetsCreds)
print('OK')

print('Getting sheet... ', end='' )
sheet = gc.open_by_key(spreadsheetID).sheet1
print('OK')
#fim da configuracao

#configurando firebase
firebase_cred = credentials.Certificate("firebase-creds.json")
firebase_admin.initialize_app(firebase_cred)
db = firestore.client()
#fim da configuracao


weekRow = sheet.find('weekId').row
activityRow = sheet.find('activityId').row


weekList = {}
weekList['id'] = sheet.find('weekId').col
weekList['tipo'] = sheet.find('tipo de evento').col
weekList['inicio'] = sheet.find('dia de início do evento').col
weekList['fim'] = sheet.find('dia de fim do evento').col
weekList['nome'] = sheet.find('título do evento').col
weekList['descricao'] = sheet.find('descrição').col
weekList['linkInscricao'] = sheet.find('link de inscrição do evento').col
weekList['linkEvento'] = sheet.find('link do evento').col
weekList['listaTipos'] = sheet.find('tipos das atividades').col

activityList = {}
activityList['id'] = sheet.find('activityId').col
activityList['nome'] = sheet.find('título da atividade').col
activityList['grupo'] = sheet.find('grupo ou empresa').col
activityList['apresentador'] = sheet.find('apresentador').col
activityList['tipo'] = sheet.find('tipo de atividade').col
activityList['local'] = sheet.find('local').col
activityList['dataInicio'] = sheet.find('dia de início').col
activityList['dataFim'] = sheet.find('dia de fim').col
activityList['horaInicio'] = sheet.find('hora de início').col
activityList['horaFim'] = sheet.find('hora de fim').col
activityList['inscricao'] = sheet.find('inscrição').col
activityList['linkInscricao'] = sheet.find('link de inscrição da atividade').col
activityList['linkRedeSocial'] = sheet.find('link de uma rede social do palestrante').col


weekId = sheet.cell(weekRow+1, weekList['id']).value

#criando o evento
evento = {}
for field in ['tipo', 'nome', 'descricao', 'linkInscricao', 'linkEvento']:
    evento[field] = sheet.cell(weekRow+1, weekList[field]).value

for dateField in ['inicio', 'fim']:
    evento[dateField] = datetime.strptime(sheet.cell(weekRow+1, weekList[dateField]).value, '%d/%m/%Y')

evento['listaTipos'] = []
for row in range(weekRow + 1, activityRow):
    tipo = sheet.cell(row, weekList['listaTipos']).value
    if tipo != '':
        evento['listaTipos'].append(tipo)
    else:
        break
#fim da cricacao do evento

print('Setting up event on firebase...', end = '')
if weekId != '':
    weekDoc = db.collection('semanas').document(weekId)
else:
    weekDoc = db.collection('semanas').document()
    weekId = weekDoc.id
    sheet.update_cell(weekRow+1, weekList['id'], weekId)

weekDoc.set(evento)
print('OK')

#criando as atividades
for row in range(activityRow + 1, len(sheet.get_all_values())+1):
    print('Setting up new activity... ', end = '')
    activityId = sheet.cell(row, activityList['id']).value
    if activityId != '':
        activityDoc = db.collection('semanas').document(weekId).collection('atividades').document(activityId)
    else:
        activityDoc = db.collection('semanas').document(weekId).collection('atividades').document()
        activityId = activityDoc.id
        sheet.update_cell(row, activityList['id'], activityId)
    activity = {}
    for field in ['nome', 'grupo', 'apresentador', 'tipo', 'local', 'inscricao', 'linkInscricao', 'linkRedeSocial']:
        activity[field] = sheet.cell(row, activityList[field]).value
    diaInicioStr = sheet.cell(row, activityList['dataInicio']).value
    diaFimStr = sheet.cell(row, activityList['dataFim']).value
    horaInicioStr = sheet.cell(row, activityList['horaInicio']).value
    horaFimStr = sheet.cell(row, activityList['horaFim']).value
    if diaFimStr == '':
        diaFimStr = diaInicioStr
    activity['inicio'] = datetime.strptime(diaInicioStr + ' ' + horaInicioStr, '%d/%m/%Y %H:%M')
    activity['fim'] = datetime.strptime(diaFimStr + ' ' + horaFimStr, '%d/%m/%Y %H:%M')
    activityDoc.set(activity)
    print('OK')
    









# data = sheet.get_all_values()

# pprint(data)
