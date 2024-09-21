def getHeader(lines):
    return lines[0].strip().split(';')[1:]


# reads the translations into a dictionary
def read_translations_csv(csv_file):
    translations = {}
    with open(csv_file) as file:
        lines = file.readlines()
        header = getHeader(lines)
        for line in lines[1:]:
            if not line.startswith("#") and not line.startswith(";"):
                property_translations = line.strip().split(';')
                property_name = property_translations[0]
                transl = property_translations[1:]
                for lang, translation in zip(header, transl):
                    if lang not in translations:
                        translations[lang] = list()
                    dic = {property_name: translation}
                    translations[lang].append(dic)
    return translations


def createsPopertyFiles(translations):
    trans = translations.items()
    # first file
    properties_file = f"./src/main/resources/messages.properties"
    with open(properties_file, 'w') as file:
        file.write(f"#Standardduebersetzung\n")
    # specific translations
    for ele in trans:
        language = ele[0]
        property_translation = ele[1]
        properties_file = f"./src/main/resources/messages_{language}.properties"
        with open(properties_file, 'w') as file:
            file.write(f"#Uebersetzungen für die Sprache {language}\n")

# writes the translations into files
# translations: language, {key=property_name : value=translation}
def generate_properties_files(translations):
    trans = translations.items()
    for ele in trans:
        language = ele[0]
        property_translation = ele[1]
        properties_file = f"./src/main/resources/messages_{language}.properties"
        with open(properties_file, "a") as file:
            for ele2 in property_translation:
                for property_name, translation in ele2.items():
                    file.write(f"{property_name}={translation}\n")


if __name__ == "__main__":
    csv_file = "uebersetzungen_v2.csv"

    translations = read_translations_csv(csv_file)
    createsPopertyFiles(translations)
    generate_properties_files(translations)
