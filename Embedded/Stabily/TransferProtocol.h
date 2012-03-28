/*
 * TransferProtocol.h
 *
 * Created: 3/28/2012 10:56:57 PM
 *  Author: david
 */ 


#ifndef TRANSFERPROTOCOL_H_
#define TRANSFERPROTOCOL_H_

/*------------------------------------------------------------------------------
 * Message Type Definitions
 */
#define TP_COMMAND_IDN					0x01
#define TP_COMMAND_GET_CAMERA_STATUS	0x02
#define TP_COMMAND_GET_CAMERA_INFO		0x03
#define TP_COMMAND_GET_STORAGE_IDS		0x04
#define TP_COMMAND_GET_STORAGE_INFO		0x05
#define TP_COMMAND_GET_SYSTEM_STATUS	0x06
#define TP_COMMAND_GET_OBJECT_INFO		0x07
#define TP_COMMAND_GET_OBJECT			0x08
#define TP_COMMAND_GET_THUMB_LIST		0x09
#define TP_COMMAND_GET_THUMB			0x0A
#define TP_COMMAND_GET_CAPTURE			0x0B
#define TP_COMMAND_GET_PROP_DESC		0x0C
#define TP_COMMAND_SET_PROP_VAL			0x0D

#define TP_EVENT_CAMERA_CONNECTED		0x31
#define TP_EVENT_CAMERA_DISCONNECTED	0x32
#define TP_EVENT_CAPTURE_FINISHED		0x33
#define TP_EVENT_OBJECT_WRITTEN			0x34
#define TP_EVENT_IN_SLEEP_MODE			0x35
#define TP_EVENT_WAKING_UP				0x36

#define TP_DATA_DEVICE_INFO				0xA1
#define TP_DATA_STORAGE_IDS				0xA2
#define TP_DATA_STORAGE_INFO			0xA3
#define TP_DATA_CAMERA_STATUS			0xA4
#define TP_DATA_OBJECT_INFO				0xA5
#define TP_DATA_OBJECT					0xA6
#define TP_DATA_THUMB_LIST				0xA7
#define TP_DATA_THUMB					0xA8
#define TP_DATA_PROP_DESC				0xA9
#define TP_DATA_PROP_VAL				0xAA
#define TP_DATA_IDN						0xAB

/*------------------------------------------------------------------------------
 * Message Definitions
 */
typedef struct 
{
	uint8_t			type;
	uint16_t		length;
	uint16_t		transID;
} TP_Header_ST;

typedef struct  
{
	TP_Header_ST	header;
	uint32_t		arg1;
	uint32_t		arg2;
	uint32_t		arg3;
} TP_Incoming_Command_ST;

typedef struct  
{
	TP_Header_ST	header;
	uint32_t		arg1;
} TP_Outgoing_Event_ST;

/*------------------------------------------------------------------------------
 * Function Definitions
 */
int TP_GetIncomingCommand ( void );


#endif /* TRANSFERPROTOCOL_H_ */