/*
 * TransferProtocolDefinitions.h
 *
 * Created: 3/31/2012 9:47:13 PM
 *  Author: david
 */ 


#ifndef TRANSFERPROTOCOLDEFINITIONS_H_
#define TRANSFERPROTOCOLDEFINITIONS_H_


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
#define TP_COMMAND_GET_THUMB			0x09
#define TP_COMMAND_CAPTURE				0x0A
#define TP_COMMAND_GET_PROP_DESC		0x0B
#define TP_COMMAND_GET_PROP_VAL			0x0C
#define TP_COMMAND_SET_PROP_VAL			0x0D
#define TP_COMMAND_SET_PROP_UPDATE		0x0E

#define TP_EVENT_CAMERA_CONNECTED		0x31
#define TP_EVENT_CAMERA_DISCONNECTED	0x32
#define TP_EVENT_CAPTURE_FINISHED		0x33
#define TP_EVENT_OBJECT_WRITTEN			0x34
#define TP_EVENT_IN_SLEEP_MODE			0x35
#define TP_EVENT_WAKING_UP				0x36
#define TP_EVENT_FRAMING_ERROR			0x37
#define TP_EVENT_PROPERTY_CHANGED		0x38

#define TP_DATA_CAMERA_INFO				0xA1
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
#define TP_DATA_PROPERTY_EVENT			0xAC
#define TP_DATA_DEBUG_LOG				0xAD

#define TP_USB_EVENT_DEVICE_ATTACHED	0x01
#define TP_USB_EVENT_DEVICE_DETTACHED	0x02
#define TP_USB_EVENT_ERROR				0x04
#define TP_USB_EVENT_CAPTURE_FINISHED	0x08
#define TP_USB_EVENT_OBJECT_WRITTEN		0x10
#define TP_USB_EVENT_PROPERTY_CHANGED	0x20
#define TP_USB_EVENT_RESERVED1			0x40
#define TP_USB_EVENT_RESERVED2			0x80

#define TP_USB_EVENT_DEVICE_ATTACHED_N	0
#define TP_USB_EVENT_DEVICE_DETTACHED_N	1
#define TP_USB_EVENT_ERROR_N			2
#define TP_USB_EVENT_CAPTURE_FINISHED_N	3
#define TP_USB_EVENT_OBJECT_WRITTEN_N	4
#define TP_USB_EVENT_PROPERTY_CHANGED_N	5
#define TP_USB_EVENT_RESERVED1_N		6
#define TP_USB_EVENT_RESERVED2_N		7

#define	TP_PROPERTY_EVENT_BatteryLevel				0x0001
#define	TP_PROPERTY_EVENT_FunctionalMode			0x0002
#define TP_PROPERTY_EVENT_WhiteBalance				0x0004
#define TP_PROPERTY_EVENT_FNumber					0x0008
#define TP_PROPERTY_EVENT_FocalLength				0x0010
#define TP_PROPERTY_EVENT_FocusDistance				0x0020
#define TP_PROPERTY_EVENT_FocusMode					0x0040
#define TP_PROPERTY_EVENT_ExposureMeteringMode		0x0080
#define TP_PROPERTY_EVENT_FlashMode					0x0100
#define TP_PROPERTY_EVENT_ExposureTime				0x0200
#define TP_PROPERTY_EVENT_ExposureProgramMode		0x0400
#define TP_PROPERTY_EVENT_ExposureIndex				0x0800
#define TP_PROPERTY_EVENT_ExposureBiasCompensation	0x1000
#define TP_PROPERTY_EVENT_RESERVED1					0x2000
#define TP_PROPERTY_EVENT_RESERVED2					0x4000
#define TP_PROPERTY_EVENT_RESERVED3					0x8000

#define	TP_PROPERTY_EVENT_BatteryLevel_N				0
#define	TP_PROPERTY_EVENT_FunctionalMode_N				1
#define TP_PROPERTY_EVENT_WhiteBalance_N				2
#define TP_PROPERTY_EVENT_FNumber_N						3
#define TP_PROPERTY_EVENT_FocalLength_N					4
#define TP_PROPERTY_EVENT_FocusDistance_N				5
#define TP_PROPERTY_EVENT_FocusMode_N					6
#define TP_PROPERTY_EVENT_ExposureMeteringMode_N		7
#define TP_PROPERTY_EVENT_FlashMode_N					8
#define TP_PROPERTY_EVENT_ExposureTime_N				9
#define TP_PROPERTY_EVENT_ExposureProgramMode_N			10
#define TP_PROPERTY_EVENT_ExposureIndex_N				11
#define TP_PROPERTY_EVENT_ExposureBiasCompensation_N	12
#define TP_PROPERTY_EVENT_RESERVED1_N					13
#define TP_PROPERTY_EVENT_RESERVED2_N					14
#define TP_PROPERTY_EVENT_RESERVED3_N					15


/*------------------------------------------------------------------------------
 * Message Definitions
 */
typedef struct 
{
	uint8_t			type;		// 1 bytes
	uint16_t		length;		// 2 bytes
	uint16_t		transID;	// 2 bytes
} TP_Header_ST;					// 5 bytes
#define TP_HEADER_SIZE	5

typedef struct  
{
	TP_Header_ST	header;		// 5 bytes
	uint32_t		arg1;		// 4 bytes
	uint32_t		arg2;		// 4 bytes
	uint32_t		arg3;		// 4 bytes
	uint8_t			checksum;	// 1 bytes
} TP_Incoming_Command_ST;		// 18 bytes
#define TP_COMMAND_SIZE	(TP_HEADER_SIZE+13)

typedef struct  
{
	TP_Header_ST	header;		// 5 bytes
	uint32_t		arg1;		// 4 bytes
} TP_Outgoing_Event_ST;			// 9 bytes
#define TP_EVENT_SIZE	(TP_HEADER_SIZE+4)

#define TP_EVENT_QUEUE_SIZE		10



#endif /* TRANSFERPROTOCOLDEFINITIONS_H_ */