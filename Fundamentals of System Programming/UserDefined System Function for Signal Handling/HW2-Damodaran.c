/***********************************************************************************************************************************************************************************************************HW1-HW2-G01129364-Damodaran.c - User Defined system funtion for signal handling and management and running command line args through child process using execlp function.

AUTHOR: Akshaya Damodaran
DATE: 12.05.2018
***********************************************************************************************************************************************************************************************************/

#include <stdio.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>

int cs531_system(char *s);

int main(int argc, char *argv[])
{
	int status;
	if(argc==1)
		status = cs531_system("No args");
	else
		status = cs531_system(argv[1]);
	return status;
}

int cs531_system(char *s)
{
	pid_t child, w;
	int status;
	void *sigPtr;

	child = fork();
	if(child == -1)
	{
		perror("fork");
		exit(EXIT_FAILURE);
	}

	if(child==0)							//Child
	{								//code executed by child process
		printf("Child PID is %ld\n", (long)getpid());
		int tt = open("/dev/tty", O_RDWR);			//redirecting STDIN, STDOUT and STDERR to monitor
		close(0); dup(tt);
		close(1); dup(tt);
		close(2); dup(tt);
		close(tt);

		if(strcmp(s,"No args")==0)
		{
			printf("Waiting for signal\n");			
			pause();
		}
		
		
		if(execlp("sh","sh","-c",s,(char*)0) < 0)		//execlp to make the child process run the command line arg
		{
			perror(s);					// print error message to terminal if shell command not found i.e execlp returns -1
			exit(1);
		}
	}
	else
	{
		printf("In parent process\n");		
		if((sigPtr=signal(SIGINT,SIG_IGN))==SIG_ERR)		//ignoring the signals in parent process
			printf("\ncan't catch SIGINT\n");
		signal(SIGQUIT,SIG_IGN);
		signal(SIGKILL,SIG_IGN);
		signal(SIGSTOP,SIG_IGN);
		signal(SIGCONT, SIG_IGN);
		do{
			
			
			w = waitpid(child, &status, WUNTRACED|WCONTINUED);
			if(w==-1)
			{
				perror("waitpid");
				exit(EXIT_FAILURE);
			}
			if(WIFEXITED(status)){
				printf("exited, status=%ld\n", (long)WEXITSTATUS(status));
			}
			else if(WIFSIGNALED(status))
			{
				printf("killed by signal %d\n", WTERMSIG(status));
				sleep(10);
				signal(SIGINT,sigPtr);			//reinstating behavior of SIGINT
			}
			else if(WIFSTOPPED(status)){
				printf("stopped by signal %d\n", WSTOPSIG(status));
			}
			else if(WIFCONTINUED(status)){
				printf("continued\n");
			}
			
			signal(SIGINT,sigPtr);				//reinstating behavior of SIGINT


		}while(!WIFEXITED(status) && !WIFSIGNALED(status));
		return status;
	}

}
