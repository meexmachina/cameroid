#include "Stabily_Shell.h"

/*------------------------------------------------------------------------------
 * Variables
 */
static uint8_t msg[MAX_MSG_SIZE];
static uint8_t last_msg[MAX_MSG_SIZE];
static uint8_t *msg_ptr;
volatile uint8_t 	g_leftDataToGet 	= 0;
volatile uint8_t	g_EchoOnOff 		= 1;

extern USB_ClassInfo_SI_Host_t DigitalCamera_SI_Interface;
extern volatile uint8_t	g_bQuiteMode;

/*------------------------------------------------------------------------------
 * cmd_t - command list
 */
static cmd_t cmd_tbl[] = 
{
	{"idn?",			tm_cmd_idn,					""					},
	{"status",			tm_cmd_status,				""					},
	{"eof",				tm_cmd_echo_off,			""					},
	{"eon",				tm_cmd_echo_on,				""					},
	{"get_dev_info",	tm_cmd_get_dev_info,		""					},
	{"get_storage_info",tm_cmd_get_storage_info,	""					},
	{"capture",			tm_cmd_capture,				""					},
	{"prop_desc", 		tm_cmd_prop_desc, 			""					},
	{"set_quite",		tm_cmd_set_quite_mode, 		"0/1"				},
	{NULL,      		NULL,						NULL				}
};


/**************************************************************************/
/*!
	This is the command prompt shell line printer command    
*/
/**************************************************************************/
void Stabily_Shell_Menu( void )
{
	if ( !g_leftDataToGet && g_EchoOnOff==1)
	{
    	printf( "\r\n");
    	printf( COMMAND_PROMPT);
	}
}

/**************************************************************************/
/*!
	This function parses the command (recieved in void rx()) and tries
	to find a suitable command line in the cmd_table. Then invokes the
	appropriate function given in the cmd_table.    
*/
/**************************************************************************/
void Stabily_Shell_Parse(char *cmd)
{
    uint8_t argc, i = 0;
    char *argv[40];

    fflush(stdout);

    argv[i] = strtok(cmd, " ");
    do
    {
        argv[++i] = strtok(NULL, " ");
    } while ((i < 40) && (argv[i] != NULL));
    
    argc = i;
    for (i=0; cmd_tbl[i].cmd != NULL; i++)
    {
        if (!strcmp(argv[0], cmd_tbl[i].cmd))
        {
            cmd_tbl[i].func(argc, argv);
            Stabily_Shell_Menu();
            return;
        }
    }
    printf( "%s: Command not recognized.\r\n", cmd);

    Stabily_Shell_Menu();
}

/**************************************************************************/
/*!
    This is the rx handler function. Collecting transmitted (rx) command
	and echoing it back. As the cmd is finished transmitting, it sends it
	to the parser

	another stuff - navigation:
		right arrow =  	0x00 -> 0x4d
		left arrow = 	0x00 -> 0x4b
		up arraw = 		0x00 -> 0x48
		down arrow = 	0x00 -> 0x50
*/
/**************************************************************************/
void Stabily_ShellRX( void )
{
	while (Serial_IsCharReceived( ))
	{
		char c = Serial_ReceiveByte ( );

	    switch (c)
	    {
		case ':':
	    case '\r':
	        // terminate the msg and reset the msg ptr. then send
	        // it to the handler for processing.

	        *msg_ptr = '\0';

			if (g_EchoOnOff)
			{
		        printf( "\r\n" );
			}				
	               			
			strcpy ((char*)(last_msg), (char*)(msg));	 
			if ( msg[0] != '\0' )	Stabily_Shell_Parse((char *)msg);
			else Stabily_Shell_Menu();
	        msg_ptr = msg;
	        break;
            
	    case '\b':
	        if (msg_ptr > msg)
	        {
	            msg_ptr--;
				Serial_SendByte ( c );
				Serial_SendByte ( ' ' );
				Serial_SendByte ( c );
			}
	        break;
			
           
	    default:
			if (g_EchoOnOff)
				Serial_SendByte ( c );

	        *msg_ptr++ = c;
	        break;
	    }
	}
}


/**************************************************************************/
/*!
	echo is off    
*/
/**************************************************************************/
void tm_cmd_echo_off(uint8_t argc, char **argv)
{
	g_EchoOnOff = 0;
	printf("OK\r\n");
}

/**************************************************************************/
/*!
	echo is on
*/
/**************************************************************************/
void tm_cmd_echo_on(uint8_t argc, char **argv)
{
	g_EchoOnOff = 1;
	printf ("OK\r\n");
}

/**************************************************************************/
/*!
	Print out an identification information   
*/
/**************************************************************************/
void tm_cmd_idn	(uint8_t argc, char **argv)
{
	printf ( "STABILY VERSION:%03d.%d;"			// The project
			 "CLOCK:%03d MHZ;"					// CPU clock speed MHz
			 "\r\n",
			STABILY_VER,
			STABILY_SUBVER,
			STABILY_CLOCK_SPEED );
}

/**************************************************************************/
/*!
	Print-out the system current status   
*/
/**************************************************************************/
void tm_cmd_status (uint8_t argc, char **argv)
{
	printf ( "Camera connected:%d\r\n", CameraControl_CameraConnected(&DigitalCamera_SI_Interface) );
}

/**************************************************************************/
/*!
	Print-out the current attached device info
*/
/**************************************************************************/
void tm_cmd_get_dev_info	(uint8_t argc, char **argv)
{
	CameraControl_DeviceInfo_Printout ( &DigitalCamera_SI_Interface );
}

/**************************************************************************/
/*!
	Print-out the current attached device's storage info   
*/
/**************************************************************************/
void tm_cmd_get_storage_info(uint8_t argc, char **argv)
{
	CameraControl_OpenSession( &DigitalCamera_SI_Interface );
	CameraControl_StorageInfo_Printout 	( &DigitalCamera_SI_Interface );
	CameraControl_CloseSession( &DigitalCamera_SI_Interface );
}

/**************************************************************************/
/*!
	Capture a still image
*/
/**************************************************************************/
void tm_cmd_capture			(uint8_t argc, char **argv)
{
 	CameraControl_DeviceOperation_Capture ( &DigitalCamera_SI_Interface,
											 PTP_ST_RemovableRAM,
											 PTP_OFC_EXIF_JPEG );
}


/**************************************************************************/
/*!
	Get a properties description
*/
/**************************************************************************/
void tm_cmd_prop_desc		(uint8_t argc, char **argv)
{
	uint16_t iPropValue;

	if ( argc < 2 )
	{
		printf("Not enough params.");
		return;
	} 

	sscanf( argv[1], "%x", &iPropValue );

 	CameraControl_DeviceOperation_GetPropertyDesc ( &DigitalCamera_SI_Interface,
													iPropValue );
}

/**************************************************************************/
/*!
	Get a properties description
*/
/**************************************************************************/
void tm_cmd_set_quite_mode	(uint8_t argc, char **argv)
{
	if (argc<2) 
	{
		printf("Need int arg");
		return;
	}

	g_bQuiteMode = (atoi (argv[1]))!=0;
}
