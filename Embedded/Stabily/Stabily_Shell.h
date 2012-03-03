#ifndef STABILY_SHELL_H
#define STABILY_SHELL_H

#include <avr/io.h>
#include <avr/wdt.h>
#include <avr/pgmspace.h>
#include <avr/power.h>
#include <avr/interrupt.h>
#include <stdio.h>

#include "CameraControl_General.h"

#define 	COMMAND_PROMPT			"STABILY >> "
#define 	MAX_MSG_SIZE   			128
#define 	STABILY_VER				1
#define		STABILY_SUBVER			0
#define		STABILY_CLOCK_SPEED		16

/*--------------------------------------------
 * Type definitions
 */
typedef struct
{
    char *cmd;
    void (*func)(uint8_t argc, char **argv);
	char *usage;
} cmd_t;

/*--------------------------------------------
 * Function definitions
 */
void Stabily_Shell_Menu( void );
void Stabily_Shell_Parse(char *cmd);
void Stabily_ShellRX( void );

/*--------------------------------------------
 * Command handler definitions
 */
void tm_cmd_echo_off		(uint8_t argc, char **argv);
void tm_cmd_echo_on			(uint8_t argc, char **argv);
void tm_cmd_idn				(uint8_t argc, char **argv);
void tm_cmd_status			(uint8_t argc, char **argv);
void tm_cmd_get_dev_info	(uint8_t argc, char **argv);
void tm_cmd_get_storage_info(uint8_t argc, char **argv);
void tm_cmd_capture			(uint8_t argc, char **argv);
void tm_cmd_prop_desc		(uint8_t argc, char **argv);


#endif // STABILY_SHELL_H
