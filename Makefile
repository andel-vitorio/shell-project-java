# Makefile para compilar e executar o programa Java

# Comandos
JAVAC = javac
JAVA = java
MKDIR = mkdir -p
CP = cp -r

# Diretórios
SRC_DIR = ./src
BIN_DIR = ./bin
LIB_DIR = ./lib
RES_DIR = $(SRC_DIR)/res

# Arquivos
MAIN_CLASS = App

# Encontre todos os arquivos JAR no diretório "lib"
JARS = $(wildcard $(LIB_DIR)/*.jar)

# Classpath com todos os arquivos JAR no diretório "lib"
CLASSPATH = $(BIN_DIR):$(JARS)

# Diretório de saída para a documentação JavaDoc
DOC_DIR = ./docs

# Lista de todos os pacotes (substitua conforme necessário)
PACKAGES := $(shell find $(SRC_DIR) -type d -exec dirname {} \; | sort -u)

# Transforma a lista de pacotes em argumentos para o JavaDoc
PACKAGES_ARG := $(addprefix -subpackages , $(PACKAGES))

.PHONY: build exec run

build:
	$(MKDIR) $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) -sourcepath $(SRC_DIR) $(SRC_DIR)/$(MAIN_CLASS).java

exec:
	$(JAVA) -cp $(CLASSPATH) $(MAIN_CLASS)

run: build exec

javadoc:
	$(MKDIR) $(DOC_DIR)
	javadoc -d $(DOC_DIR) -sourcepath $(SRC_DIR) $(PACKAGES_ARG)

clean:
	rm -rf $(BIN_DIR)
