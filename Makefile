# implement later for deterministic sources

# UTILS_FILES = src1 src2
# OBJ1_FILES = src1 src2
# CLIENT_SRCS = $(addprefix src/utilsClient/,$(UTILS_FILES))
# SERVER_SRCS = $(addprefix src/obj1/,$(OBJ1_FILES))

# SRCS = $(CLIENT_SRCS) $(SERVER_SRCS)
# SRCS = $(addsuffix .c,$(SRCS))

#   ______                                 __ __            __     __                   
#  /      \                               |  \  \          |  \   |  \                  
# |  ▓▓▓▓▓▓\ ______  ______ ____   ______  \▓▓ ▓▓ ______  _| ▓▓_   \▓▓ ______  _______  
# | ▓▓   \▓▓/      \|      \    \ /      \|  \ ▓▓|      \|   ▓▓ \ |  \/      \|       \ 
# | ▓▓     |  ▓▓▓▓▓▓\ ▓▓▓▓▓▓\▓▓▓▓\  ▓▓▓▓▓▓\ ▓▓ ▓▓ \▓▓▓▓▓▓\\▓▓▓▓▓▓ | ▓▓  ▓▓▓▓▓▓\ ▓▓▓▓▓▓▓\ 
# | ▓▓   __| ▓▓  | ▓▓ ▓▓ | ▓▓ | ▓▓ ▓▓  | ▓▓ ▓▓ ▓▓/      ▓▓ | ▓▓ __| ▓▓ ▓▓  | ▓▓ ▓▓  | ▓▓
# | ▓▓__/  \ ▓▓__/ ▓▓ ▓▓ | ▓▓ | ▓▓ ▓▓__/ ▓▓ ▓▓ ▓▓  ▓▓▓▓▓▓▓ | ▓▓|  \ ▓▓ ▓▓__/ ▓▓ ▓▓  | ▓▓
#  \▓▓    ▓▓\▓▓    ▓▓ ▓▓ | ▓▓ | ▓▓ ▓▓    ▓▓ ▓▓ ▓▓\▓▓    ▓▓  \▓▓  ▓▓ ▓▓\▓▓    ▓▓ ▓▓  | ▓▓
#   \▓▓▓▓▓▓  \▓▓▓▓▓▓ \▓▓  \▓▓  \▓▓ ▓▓▓▓▓▓▓ \▓▓\▓▓ \▓▓▓▓▓▓▓   \▓▓▓▓ \▓▓ \▓▓▓▓▓▓ \▓▓   \▓▓
#                                | ▓▓                                                   
#                                | ▓▓                                                   
#                                 \▓▓                                                   

MAIN = Main
OUT_DIR = out

# sources and libs
SOURCES = $(shell find src -type f -name "*.java")
LIBS 	= $(shell find lib -type f -name "*.jar" | tr '\n' ':' | sed 's/:$$//')
CLASSES := $(patsubst src/%.java,$(OUT_DIR)/%.class,$(SOURCES))

# compilation
JAVAC_FLAGS	= -Xlint:all -Werror $(JPPROJECT_FLAGS)
JAVA_FLAGS 	= --enable-native-access=ALL-UNNAMED
# Classpath for java (Linux/Mac)   # CP = $(OUT_DIR);$(LIBS) | tr '\n' ':'# Uncomment for Windows
CP = $(OUT_DIR):$(LIBS) 

# MAKEFILE
all: $(CLASSES)
	@$(call print_success,"")

$(OUT_DIR):
	@$(call print_info,"creating $(OUT_DIR)/")
	mkdir -p $(OUT_DIR)

# Convert src/Path/File.java → out/Path/File.class
$(OUT_DIR)/%.class: src/%.java | $(OUT_DIR) $(JLINE_JAR) $(LIB_EK_JAR)
	@$(call print_info,"Compiling $< ...")
	@mkdir -p $(dir $@)         # Ensure the directory exists
	javac $(JAVAC_FLAGS) -cp "$(CP)" -d $(OUT_DIR) $<

clean:
	@$(call print_info,"Cleaning build directory...")
	rm -rf $(OUT_DIR)

re: clean all

clean_libs:
	rm -rf $(LIB_DIR)

clean_externals:
	rm -rf $(EXTERNAL_DIR)

.PHONY: all clean clean_externals clean_libs clear re 
#                                                 __     __                   
#                                                |  \   |  \                  
#   ______  __    __  ______   _______ __    __ _| ▓▓_   \▓▓ ______  _______  
#  /      \|  \  /  \/      \ /       \  \  |  \   ▓▓ \ |  \/      \|       \ 
# |  ▓▓▓▓▓▓\\▓▓\/  ▓▓  ▓▓▓▓▓▓\  ▓▓▓▓▓▓▓ ▓▓  | ▓▓\▓▓▓▓▓▓ | ▓▓  ▓▓▓▓▓▓\ ▓▓▓▓▓▓▓\ 
# | ▓▓    ▓▓ >▓▓  ▓▓| ▓▓    ▓▓ ▓▓     | ▓▓  | ▓▓ | ▓▓ __| ▓▓ ▓▓  | ▓▓ ▓▓  | ▓▓
# | ▓▓▓▓▓▓▓▓/  ▓▓▓▓\| ▓▓▓▓▓▓▓▓ ▓▓_____| ▓▓__/ ▓▓ | ▓▓|  \ ▓▓ ▓▓__/ ▓▓ ▓▓  | ▓▓
#  \▓▓     \  ▓▓ \▓▓\\▓▓     \\▓▓     \\▓▓    ▓▓  \▓▓  ▓▓ ▓▓\▓▓    ▓▓ ▓▓  | ▓▓
#   \▓▓▓▓▓▓▓\▓▓   \▓▓ \▓▓▓▓▓▓▓ \▓▓▓▓▓▓▓ \▓▓▓▓▓▓    \▓▓▓▓ \▓▓ \▓▓▓▓▓▓ \▓▓   \▓▓
# 
                                                               
run: all
	@$(call print_info,"Running the program...")
	@echo "java $(JAVA_FLAGS) -cp \"$(CP)\" $(MAIN)"
	@echo "$(SUCC)[DONE]$(RESET) $(1)$(RESET)"
	@echo ""
	@java $(JAVA_FLAGS) -cp "$(CP)" $(MAIN)

rerun: re run

.PHONY: run rerun 

#  __ __ __                                  __                   
# |  \  \  \                                |  \                  
# | ▓▓\▓▓ ▓▓____   ______   ______   ______  \▓▓ ______   _______ 
# | ▓▓  \ ▓▓    \ /      \ |      \ /      \|  \/      \ /       \ 
# | ▓▓ ▓▓ ▓▓▓▓▓▓▓\  ▓▓▓▓▓▓\ \▓▓▓▓▓▓\  ▓▓▓▓▓▓\ ▓▓  ▓▓▓▓▓▓\  ▓▓▓▓▓▓▓
# | ▓▓ ▓▓ ▓▓  | ▓▓ ▓▓   \▓▓/      ▓▓ ▓▓   \▓▓ ▓▓ ▓▓    ▓▓\▓▓    \ 
# | ▓▓ ▓▓ ▓▓__/ ▓▓ ▓▓     |  ▓▓▓▓▓▓▓ ▓▓     | ▓▓ ▓▓▓▓▓▓▓▓_\▓▓▓▓▓▓\ 
# | ▓▓ ▓▓ ▓▓    ▓▓ ▓▓      \▓▓    ▓▓ ▓▓     | ▓▓\▓▓     \       ▓▓
#  \▓▓\▓▓\▓▓▓▓▓▓▓ \▓▓       \▓▓▓▓▓▓▓\▓▓      \▓▓ \▓▓▓▓▓▓▓\▓▓▓▓▓▓▓ 
#                                                              


#libraries
EXTERNAL_DIR	= external
LIB_DIR			= lib

#JLINE_VER = 3.25.1
#JLINE_JAR = $(LIB_DIR)/jline-$(JLINE_VER).jar
#JLINE_URL = https://repo1.maven.org/maven2/org/jline/jline/$(JLINE_VER)/jline-$(JLINE_VER).jar

#fixme: get a specific hash
EXTERNAL_EK_LIB_DIR	= $(EXTERNAL_DIR)/lib-java-ekrebs
LIB_EK_URL			= git@github.com:M0rgenstern-ekrebs/lib-java-ekrebs.git
LIB_EK_JAR_FILENAME	= lib_ekrebs.jar
LIB_EK_JAR			= $(LIB_DIR)/$(LIB_EK_JAR_FILENAME)

liblist:
	@$(call print_info,"Listing all JAR contents...")
	jar tf $(LIBS)

$(LIB_DIR):
	@$(call print_info,"creating $(LIB_DIR)/")
	mkdir -p $(LIB_DIR)

$(EXTERNAL_DIR):
	@$(call print_info,"creating $(EXTERNAL_DIR)/")
	mkdir -p $(EXTERNAL_DIR)

$(JLINE_JAR): | $(LIB_DIR)
	@$(call print_info,"getting Jline lib .jar")
	wget -O $(JLINE_JAR) $(JLINE_URL)

$(EXTERNAL_EK_LIB_DIR): | $(LIB_DIR) $(EXTERNAL_DIR)
	@$(call print_info,"getting lib_ek repo")
	git clone $(LIB_EK_URL) $(EXTERNAL_EK_LIB_DIR)

$(LIB_EK_JAR): | $(EXTERNAL_EK_LIB_DIR)
	@$(call print_info,"building lib_ek .jar")
	$(MAKE) $(LIB_EK_JAR_FILENAME) -C $(EXTERNAL_EK_LIB_DIR)
	mv $(EXTERNAL_EK_LIB_DIR)/$(LIB_EK_JAR_FILENAME) $(LIB_DIR)/$(LIB_EK_JAR_FILENAME)

.PHONY: liblist

#             __     __ __ __   __                       __                   
#            |  \   |  \  \  \ |  \                     |  \                  
#  __    __ _| ▓▓_   \▓▓ ▓▓\▓▓_| ▓▓_    ______   ______  \▓▓ ______   _______ 
# |  \  |  \   ▓▓ \ |  \ ▓▓  \   ▓▓ \  |      \ /      \|  \/      \ /       \ 
# | ▓▓  | ▓▓\▓▓▓▓▓▓ | ▓▓ ▓▓ ▓▓\▓▓▓▓▓▓   \▓▓▓▓▓▓\  ▓▓▓▓▓▓\ ▓▓  ▓▓▓▓▓▓\  ▓▓▓▓▓▓▓
# | ▓▓  | ▓▓ | ▓▓ __| ▓▓ ▓▓ ▓▓ | ▓▓ __ /      ▓▓ ▓▓   \▓▓ ▓▓ ▓▓    ▓▓\▓▓    \ 
# | ▓▓__/ ▓▓ | ▓▓|  \ ▓▓ ▓▓ ▓▓ | ▓▓|  \  ▓▓▓▓▓▓▓ ▓▓     | ▓▓ ▓▓▓▓▓▓▓▓_\▓▓▓▓▓▓\ 
#  \▓▓    ▓▓  \▓▓  ▓▓ ▓▓ ▓▓ ▓▓  \▓▓  ▓▓\▓▓    ▓▓ ▓▓     | ▓▓\▓▓     \       ▓▓
#   \▓▓▓▓▓▓    \▓▓▓▓ \▓▓\▓▓\▓▓   \▓▓▓▓  \▓▓▓▓▓▓▓\▓▓      \▓▓ \▓▓▓▓▓▓▓\▓▓▓▓▓▓▓ 
# 

#  ▄▀▀ ▄▀▄ █   ▄▀▄ █▀▄ █▀
#  ▀▄▄ ▀▄▀ █▄▄ ▀▄▀ █▀▄ ▄█

INFO = \033[1;34m
WARN = \033[1;33m
ERR = \033[1;31m
SUCC = \033[1;32m
RESET = \033[0m


#  █▀▄ █▀▄ █ █▄ █ ▀█▀ ██▀ █▀▄ ▄▀▀
#  █▀  █▀▄ █ █ ▀█  █  █▄▄ █▀▄ ▄██

define print_info
	@echo "$(INFO)[INFO]$(RESET) $(1)$(RESET)"
endef

define print_debug
	@echo "$(INFO)[DEBUG]$(WARN) $(1)$(RESET)"
endef

define print_warn
	@echo "$(WARN)[WARN]$(WARN) $(1)$(RESET)"
endef

define print_err
	@echo "$(ERR)[ERR]$(WARN) $(1)$(RESET)"
endef

define print_success
	@echo "$(SUCC)[DONE]$(RESET) $(1)$(RESET)"
endef

#  ▄▀▀ █ █ ▄▀▀ ▀█▀ ▄▀▄ █▄ ▄█
#  ▀▄▄ ▀▄█ ▄██  █  ▀▄▀ █ ▀ █

flammenwerfer_printer: 
	@$(call print_warn,"flammenwerfer !")

achtung_printer:
	@$(call print_warn,"achtung !")

jawohl_printer:
	@$(call print_warn,"jawohl !")

flammenwerfer: flammenwerfer_printer clean clean_externals clean_libs

achtung: achtung_printer flammenwerfer all

#to take into account local quick lib edit
jawohl: jawohl_printer clean_libs run

german_boy_printer:
	@$(call print_warn,"no little german boy ! don\'t check out the validity of the dependencies !")
	@$(call print_warn,"mein gott... but das ist ein bunch of bullsheiBe... !")

#if you're annoyed you change doesn't seem to register, burn it to the ground, refetch the libraries, rebuild the project rerun it
german_boy: german_boy_printer achtung run

.PHONY: flammenwerfer flammenwerfer_printer jawohl jawohl_printer achtung achtung_printer