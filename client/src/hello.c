
#include <stdlib.h>
#include <stdio.h>

int main(int argc, char *argv[])
{
    FILE* fichier = NULL;
 
    fichier = fopen("test.txt", "w");
 
    if (fichier != NULL)
    {
        fputs("Salut les Zér0s\nComment allez-vous", fichier);
        fclose(fichier);
    }

    printf("hello\n");
 
    return 0;
}