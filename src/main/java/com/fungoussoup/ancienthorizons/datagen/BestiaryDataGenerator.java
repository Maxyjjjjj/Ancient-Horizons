package com.fungoussoup.ancienthorizons.datagen;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Data generator for creating bestiary entry files
 */
public class BestiaryDataGenerator implements DataProvider {
    private final PackOutput output;
    private final List<BestiaryEntryBuilder> entries = new ArrayList<>();

    public BestiaryDataGenerator(PackOutput output) {
        this.output = output;
    }

    /**
     * Builder class for creating bestiary entries
     */
    public static class BestiaryEntryBuilder {
        private final String entityId;
        private final Map<String, Translation> translations = new HashMap<>();

        public BestiaryEntryBuilder(String entityId) {
            this.entityId = entityId;
        }

        public BestiaryEntryBuilder addTranslation(String language, String name, String scientific,
                                                   String period, String description) {
            translations.put(language, new Translation(name, scientific, period, description));
            return this;
        }

        public String getEntityId() {
            return entityId;
        }

        public Map<String, Translation> getTranslations() {
            return translations;
        }

        public record Translation(String name, String scientific, String period, String description) {
            public String toFileContent() {
                return "Name: " + name + "\n" +
                        "Scientific: " + scientific + "\n" +
                        "Period: " + period + "\n" +
                        "Description:\n" +
                        description;
            }
        }
    }

    /**
     * Add all your bestiary entries here
     */
    protected void addEntries() {
        // MODDED ENTITIES

        // Tiger (already exists, but showing format)
        addEntry("tiger")
                .addTranslation("en_us", "Tiger", "Panthera tigris tigris", "Modern",
                        """
                                The tiger is the largest modern cat, distinguished by its striped fur and immense strength. \
                                It can be found exclusively in the taiga.
                                They hunt cows, pigs, sheep and other animals, using their strength and keen eyesight.
                                Tigers are solitary animals that occupy large territories. They are known for their ability to swim \
                                and often bathe in rivers and lakes to cool off in hot weather.
                                A tiger can be tamed with meat, and a tamed tiger is stronger than a tamed wolf or snow leopard.
                                But be careful: if you hit a wild tiger, it will become hostile and attack you.
                                Tigers are excellent parents who protect their cubs from any threat.
                                Tigers naturally come in different colors, including orange, white, golden, and even blue! \
                                And there is a purple tiger, but it requires naming a tiger "Legends".""")
                .addTranslation("uk_ua", "Тигр", "Panthera tigris tigris", "Сучасність",
                        """
                                Тигр — найбільший сучасний кіт, вирізняється смугастим хутром та величезною силою. \
                                Його можна зустріти виключно у тайзі.
                                Вони полюють на корів, свиней, овець та інших тварин, використовуючи свою потужність і гострий зір.
                                Тигри є самотніми тваринами, які займають великі території. Вони відомі своєю здатністю плавати \
                                й часто купаються в річках та озерах, щоб охолодитися в спекотну погоду.
                                Тигра можна приручити за допомогою м'яса, і приручений тигр буде сильніший за прирученого вовка чи снігового барса.
                                Але будьте обережні: якщо ви вдарите дикого тигра, він стане ворожим і буде атакувати вас.
                                Тигри є чудовими батьками, які захищають тигренят від будь-якої загрози.
                                Тигри бувають різних кольорів, зокрема помаранчевого, білого, золотистого і навіть синього! \
                                А ще є фіолетовий тигр, для отримання якого, тигра потрібно назвати «Legends».""");

        // Snow Leopard
        addEntry("snow_leopard")
                .addTranslation("en_us", "Snow Leopard", "Panthera uncia", "Modern",
                        """
                                The snow leopard is a magnificent big cat adapted to life in harsh mountain environments.
                                Its thick fur and long tail help it survive in cold climates.
                                Found in snowy mountain biomes, snow leopards are agile hunters capable of taking down prey much larger than themselves.
                                They can be tamed with meat. Tamed snow leopards can wear armor for protection.
                                These elusive cats are excellent climbers and can leap great distances across rocky terrain.""")
                .addTranslation("uk_ua", "Сніговий барс", "Panthera uncia", "Сучасність",
                        """
                                Сніговий барс — чудовий великий кіт, пристосований до життя в суворих гірських умовах.
                                Його густе хутро та довгий хвіст допомагають виживати в холодному кліматі.
                                Снігові барси, що мешкають у засніжених гірських біомах, є спритними мисливцями, здатними полювати на здобич набагато більшу за них.
                                Їх можна приручити м'ясом. Приручені снігові барси можуть носити броню для захисту.
                                Ці невловимі коти чудово лазять і можуть стрибати на великі відстані по скелястій місцевості.""");

        // Lion
        addEntry("lion")
                .addTranslation("en_us", "Lion", "Panthera leo", "Modern",
                        """
                                The lion is known as the king of beasts, famous for its majestic mane and powerful roar. \
                                Males have impressive manes while females do the majority of hunting.
                                Found in savannas and grasslands, lions are social animals that live in prides. They are apex predators \
                                that hunt in coordinated groups.
                                Lions can be tamed with meat. Tamed lions are loyal protectors and formidable companions in battle.
                                Their roar can be heard from miles away and serves to establish territory and communicate with pride members.""")
                .addTranslation("uk_ua", "Лев", "Panthera leo", "Сучасність",
                        """
                                Лев відомий як король звірів, славиться своєю величною гривою та потужним ревом. \
                                Самці мають вражаючі гриви, тоді як самки здійснюють більшість полювань.
                                Леви, що мешкають у саванах і луках, є соціальними тваринами, які живуть у прайдах. Це хижаки вищого рівня, які полюють координованими групами.
                                Левів можна приручити м'ясом. Приручені леви є вірними захисниками та грізними супутниками в бою.
                                Їхній рев можна почути за кілька кілометрів, і він служить для встановлення території та спілкування з членами прайду.""");

        // Elephant
        addEntry("elephant")
                .addTranslation("en_us", "Elephant", "Loxodonta africana", "Modern",
                        """
                                Elephants are the largest land animals on Earth, known for their intelligence, strong family bonds, and distinctive trunks.
                                These gentle giants roam savannas in herds, using their trunks to gather food and water. \
                                Despite their peaceful nature, they will charge when threatened.
                                Elephants can be tamed and used as mounts, capable of carrying multiple passengers. \
                                They have excellent memory and form deep bonds with their caretakers.
                                Their ivory tusks are valuable but should be protected, not harvested.""")
                .addTranslation("uk_ua", "Слон", "Loxodonta africana", "Сучасність",
                        """
                                Слони — найбільші наземні тварини на Землі, відомі своїм інтелектом, міцними сімейними зв'язками \
                                та характерними хоботами.
                                Ці лагідні велетні мандрують саванами стадами, використовуючи хоботи для збирання їжі та води. \
                                Незважаючи на свій мирний характер, вони атакують, коли їм загрожують.
                                Слонів можна приручити та використовувати як їздових тварин, здатних нести кількох пасажирів. \
                                Вони мають чудову пам'ять і формують глибокі зв'язки зі своїми опікунами.
                                Їхні слонові бивні цінні, але їх слід захищати, а не збирати.""");

        // Zebra
        addEntry("zebra")
                .addTranslation("en_us", "Zebra", "Equus quagga", "Modern",
                        """
                                Zebras are distinctive members of the horse family, easily recognized by their bold black and white stripes. \
                                Each zebra has a unique stripe pattern, like a fingerprint.
                                Found in African savanna biomes, zebras are social animals that live in herds for protection against predators.
                                Zebras can be tamed similarly to horses and can be bred with horses to create zorses, \
                                or with donkeys to create zonkeys.
                                Their stripes may help confuse predators and regulate body temperature in the hot savanna climate.""")
                .addTranslation("uk_ua", "Зебра", "Equus quagga", "Сучасність",
                        """
                                Зебри — характерні представники родини коневих, які легко впізнаються за виразними чорно-білими смугами. \
                                Кожна зебра має унікальний візерунок смуг, як відбиток пальця.
                                Зебри, що живуть в африканських біомах савани, є соціальними тваринами, які живуть стадами для захисту від хижаків.
                                Зебр можна приручити подібно до коней, і їх можна схрещувати з конями для створення зорсів, \
                                або з віслюками для створення зонкі.
                                Їхні смуги можуть допомагати заплутувати хижаків і регулювати температуру тіла в жаркому кліматі савани.""");

        // VANILLA ENTITIES

        // Cow
        addEntry("cow")
                .addTranslation("en_us", "Cow", "Bos taurus", "Modern (Domesticated ~10,000 years ago)",
                        """
                                Cows are domesticated bovines raised for milk, meat, and leather. They are one of humanity's most important livestock animals.
                                Found in plains and forest biomes, cows are peaceful herbivores that graze on grass. They can be bred using wheat.
                                Cows provide leather when killed, and can be milked using a bucket to obtain milk.
                                These gentle creatures are descendants of wild aurochs and have been selectively bred for thousands of years.""")
                .addTranslation("uk_ua", "Корова", "Bos taurus", "Сучасність (Одомашнена ~10,000 років тому)",
                        """
                                Корови — одомашнені бовини, що розводяться для отримання молока, м'яса та шкіри. \
                                Вони є одними з найважливіших свійських тварин людства.
                                Корови, що живуть на рівнинах і лісових біомах, є мирними травоїдними, які пасуться на траві. Їх можна розводити за допомогою пшениці.
                                Корови дають шкіру при забої, а молоко можна отримати за допомогою відра.
                                Ці лагідні створіння є нащадками диких турів і селекційно розводилися протягом тисячоліть.""");

        // Pig
        addEntry("pig")
                .addTranslation("en_us", "Pig", "Sus scrofa domesticus", "Modern (Domesticated ~9,000 years ago)",
                        """
                                Pigs are intelligent domesticated animals raised primarily for meat. Despite their reputation, pigs are actually quite clean animals.
                                Found in most overworld biomes, pigs are omnivores that will eat almost anything. They can be bred using carrots, potatoes, or beetroots.
                                Pigs can be ridden when equipped with a saddle and directed using a carrot on a stick.
                                These social animals are smarter than dogs and enjoy playing in mud to stay cool and protect their skin from parasites.""")
                .addTranslation("uk_ua", "Свиня", "Sus scrofa domesticus", "Сучасність (Одомашнена ~9,000 років тому)",
                        """
                                Свині — розумні одомашнені тварини, що розводяться переважно для м'яса. Всупереч своїй репутації, свині насправді досить чисті тварини.
                                Свині, що живуть у більшості наземних біомів, є всеїдними і з'їдять майже все. Їх можна розводити за допомогою моркви, картоплі або буряків.
                                На свинях можна їздити, якщо їх оснастити сідлом і керувати за допомогою моркви на палиці.
                                Ці соціальні тварини розумніші за собак і люблять гратися в багнюці, щоб охолодитися та захистити шкіру від паразитів.""");

        // Chicken
        addEntry("chicken")
                .addTranslation("en_us", "Chicken", "Gallus gallus domesticus", "Modern (Domesticated ~6,000 years ago)",
                        """
                                Chickens are domesticated birds raised for eggs and meat. They are the most common bird species in the world.
                                Found in most biomes, chickens wander aimlessly and lay eggs periodically. They can be bred using seeds of any kind.
                                Chickens provide feathers and raw chicken when killed. Their eggs can be thrown to potentially spawn baby chicks.
                                These birds are descended from jungle fowl and have been bred into hundreds of varieties worldwide.""")
                .addTranslation("uk_ua", "Курка", "Gallus gallus domesticus", "Сучасність (Одомашнена ~6,000 років тому)",
                        """
                                Кури — одомашнені птахи, що розводяться для яєць і м'яса. Вони є найпоширенішим видом птахів у світі.
                                Кури, що живуть у більшості біомів, безцільно блукають і періодично несуть яйця. Їх можна розводити за допомогою будь-яких насінин.
                                Кури дають пір'я та сиру курятину при забої. Їхні яйця можна кидати, щоб потенційно викликати курчат.
                                Ці птахи походять від джунглевих курей і були виведені у сотні сортів по всьому світу.""");

        // Sheep
        addEntry("sheep")
                .addTranslation("en_us", "Sheep", "Ovis aries", "Modern (Domesticated ~10,000 years ago)",
                        """
                                Sheep are domesticated mammals raised primarily for wool, meat, and milk. Their wool can be dyed in many colors.
                                Found in plains and mountain biomes, sheep graze on grass to regrow their wool. They can be bred using wheat.
                                Sheep can be sheared for 1-3 blocks of wool without harming them. They naturally spawn in white and sometimes in other colors.
                                These docile animals have been essential to human civilization, providing materials for clothing and textiles.""")
                .addTranslation("uk_ua", "Вівця", "Ovis aries", "Сучасність (Одомашнена ~10,000 років тому)",
                        """
                                Вівці — одомашнені ссавці, що розводяться переважно для вовни, м'яса та молока. Їхню вовну можна фарбувати в багато кольорів.
                                Вівці, що живуть на рівнинах і в гірських біомах, пасуться на траві, щоб відростити вовну. Їх можна розводити за допомогою пшениці.
                                Вівець можна стригти, отримуючи 1-3 блоки вовни без шкоди для них. Вони природно з'являються білими, а іноді й іншими кольорами.
                                Ці лагідні тварини були важливими для людської цивілізації, забезпечуючи матеріали для одягу та текстилю.""");

        // Wolf
        addEntry("wolf")
                .addTranslation("en_us", "Wolf", "Canis lupus", "Modern",
                        """
                                Wolves are wild canines that hunt in packs across various biomes. They are the ancestors of domestic dogs.
                                Found in forests and taiga biomes, wolves hunt in coordinated packs and can be formidable enemies when provoked.
                                Wolves can be tamed using bones, after which they become loyal companions. Tamed wolves will attack enemies and can be ordered to sit.
                                They can be bred using meat and will assist their owner in combat. Wolves come in several natural variants depending on their biome.""")
                .addTranslation("uk_ua", "Вовк", "Canis lupus", "Сучасність",
                        """
                                Вовки — дикі псові, що полюють зграями в різних біомах. Вони є предками домашніх собак.
                                Вовки, що живуть у лісах і тайзі, полюють координованими зграями і можуть бути грізними ворогами, коли їх провокують.
                                Вовків можна приручити за допомогою кісток, після чого вони стають вірними супутниками. Приручені вовки атакують ворогів і їм можна наказати сидіти.
                                Їх можна розводити за допомогою м'яса, і вони допомагатимуть своєму господарю в бою. Вовки мають кілька природних варіантів залежно від біому.""");

        // Horse
        addEntry("horse")
                .addTranslation("en_us", "Horse", "Equus ferus caballus", "Modern (Domesticated ~6,000 years ago)",
                        """
                                Horses are majestic animals that have served humans for transportation and companionship for millennia.
                                Found in plains and savanna biomes, horses vary in color, speed, and jumping ability. They can be tamed by repeatedly mounting them.
                                Once tamed, horses can be equipped with saddles for riding and armor for protection. They can be bred using golden apples or carrots.
                                Horses are faster than walking and can jump over obstacles, making them excellent mounts for exploration.""")
                .addTranslation("uk_ua", "Кінь", "Equus ferus caballus", "Сучасність (Одомашнений ~6,000 років тому)",
                        """
                                Коні — величні тварини, які служили людям для транспорту та супутництва протягом тисячоліть.
                                Коні, що живуть на рівнинах і в саванах, відрізняються кольором, швидкістю та здатністю стрибати. Їх можна приручити, багаторазово сідаючи на них.
                                Після приручення коней можна оснастити сідлами для їзди та бронею для захисту. Їх можна розводити за допомогою золотих яблук або моркви.
                                Коні швидші за ходьбу і можуть перестрибувати перешкоди, що робить їх чудовими їздовими тваринами для дослідження.""");

        addEntry("diplodocus")
                .addTranslation("en_us", "Diplodocus", "Diplodocus carnegii", "Late Jurassic (155-145 million years ago)",
                        """
                                Diplodocus is one of the longest dinosaurs ever discovered, with some specimens reaching over 25 meters in length. \
                                Its distinctive long neck and whip-like tail make it easily recognizable.
                                These gentle giants were herbivores that fed on low-lying vegetation and could rear up on their hind legs to reach higher branches.
                                Diplodocus can be tamed and ridden once fully grown, making them excellent mounts for crossing large distances. They are peaceful unless provoked.
                                Despite their massive size, Diplodocus were relatively light for their length due to hollow bones, similar to modern birds.""")
                .addTranslation("uk_ua", "Диплодок", "Diplodocus carnegii", "Пізня юра (155-145 мільйонів років тому)",
                        """
                                Диплодок — один з найдовших динозаврів, коли-небудь відкритих, деякі екземпляри досягали понад 25 метрів у довжину. \
                                Його характерна довга шия та хвіст, схожий на батіг, роблять його легко впізнаваним.
                                Ці лагідні велетні були травоїдними, які харчувалися низькою рослинністю і могли підніматися на задні лапи, щоб дістати вищі гілки.
                                Диплодока можна приручити та їздити на ньому після повного виросту, що робить їх чудовими їздовими тваринами для подолання великих відстаней. Вони мирні, якщо їх не провокувати.
                                Незважаючи на свій масивний розмір, диплодоки були відносно легкими для своєї довжини завдяки порожнистим кісткам, подібним до сучасних птахів.""");

        addEntry("gallimimus")
                .addTranslation("en_us", "Gallimimus", "Gallimimus bullatus", "Late Cretaceous (70-66 million years ago)",
                        """
                                Gallimimus was a fast-running ornithomimid dinosaur, resembling a large flightless bird. Its name means "chicken mimic" due to its bird-like appearance.
                                These omnivorous dinosaurs could run at high speeds, making them excellent for quick transportation once tamed.
                                Found in plains and savanna biomes, Gallimimus are skittish creatures that will flee when approached. They can be tamed using seeds and vegetables.
                                Their long legs and lightweight build made them one of the fastest dinosaurs, capable of outrunning most predators.""")
                .addTranslation("uk_ua", "Галімім", "Gallimimus bullatus", "Пізня крейда (70-66 мільйонів років тому)",
                        """
                                Галімім був швидкобіжним орнітомімідним динозавром, схожим на велику нелітаючу птицю. Його назва означає «імітатор курки» через його птахоподібний вигляд.
                                Ці всеїдні динозаври могли бігати на високих швидкостях, що робить їх чудовими для швидкого транспортування після приручення.
                                Галіміми, що живуть на рівнинах і в саванах, є боязкими створіннями, які втікають при наближенні. Їх можна приручити за допомогою насіння та овочів.
                                Їхні довгі ноги та легка будова зробили їх одними з найшвидших динозаврів, здатних випередити більшість хижаків.""");

        addEntry("velociraptor")
                .addTranslation("en_us", "Velociraptor", "Velociraptor mongoliensis", "Late Cretaceous (75-71 million years ago)",
                        """
                                Velociraptor was a small but intelligent theropod dinosaur known for its distinctive sickle-shaped claw on each foot. \
                                Despite popular depictions, they were actually turkey-sized and covered in feathers.
                                These pack hunters are highly aggressive and will attack in coordinated groups. They are found in desert and badlands biomes.
                                Velociraptors can be tamed with raw meat, but require patience and skill. Once tamed, they become fierce protectors and excellent hunting companions.
                                Their intelligence and pack-hunting behavior made them formidable predators despite their small size.""")
                .addTranslation("uk_ua", "Велоцираптор", "Velociraptor mongoliensis", "Пізня крейда (75-71 мільйонів років тому)",
                        """
                                Велоцираптор був невеликим, але розумним тероподним динозавром, відомим своїм характерним серповидним кігтем на кожній лапі. \
                                Всупереч популярним зображенням, вони насправді були розміром з індика і вкриті пір'ям.
                                Ці зграйні мисливці дуже агресивні і атакують координованими групами. Їх можна знайти в пустелях і бедлендах.
                                Велоцирапторів можна приручити сирим м'ясом, але це вимагає терпіння та майстерності. Після приручення вони стають лютими захисниками та чудовими мисливськими супутниками.
                                Їхній інтелект та зграйна поведінка при полюванні зробили їх грізними хижаками, незважаючи на їхній малий розмір.""");

        addEntry("crocodile")
                .addTranslation("en_us", "Crocodile", "Crocodylus niloticus", "Modern (Ancient lineage 95 million years)",
                        """
                                Crocodiles are ancient reptiles that have remained largely unchanged for millions of years. They are ambush predators that lurk in water waiting for prey.
                                These reptiles cannot be tamed. They are most active during dawn and dusk.
                                Crocodiles have the strongest bite force of any living animal and can hold their breath underwater for extended periods.""")
                .addTranslation("uk_ua", "Крокодил", "Crocodylus niloticus", "Сучасність (Давня лінія 95 мільйонів років)",
                        """
                                Крокодили — давні рептилії, які залишалися практично незмінними протягом мільйонів років. Вони є хижаками-засідниками, які ховаються у воді, чекаючи здобич.
                                Цих рептилій не можна приручити. Вони найбільш активні на світанку та в сутінках.
                                Крокодили мають найсильнішу силу укусу серед усіх живих тварин і можуть затримувати подих під водою протягом тривалого часу.""");

        addEntry("penguin")
                .addTranslation("en_us", "Penguin", "Aptenodytes forsteri", "Modern (Evolved ~60 million years ago)",
                        """
                                Penguins are flightless seabirds adapted for life in cold climates. Their distinctive black and white coloring provides camouflage in the water.
                                Found in frozen ocean and snowy beach biomes, penguins are excellent swimmers that hunt fish underwater.
                                Penguins are social animals that live in colonies. They can be bred using fish and will huddle together for warmth during storms.
                                These charismatic birds slide on their bellies across ice and can dive to impressive depths while hunting.""")
                .addTranslation("uk_ua", "Пінгвін", "Aptenodytes forsteri", "Сучасність (Еволюціонували ~60 мільйонів років тому)",
                        """
                                Пінгвіни — нелітаючі морські птахи, пристосовані до життя в холодному кліматі. Їхнє характерне чорно-біле забарвлення забезпечує камуфляж у воді.
                                Пінгвіни, що живуть у замерзлих океанах і засніжених пляжних біомах, є чудовими плавцями, які полюють на рибу під водою.
                                Пінгвіни — соціальні тварини, які живуть колоніями. Їх можна розводити за допомогою риби, і вони збираються разом для тепла під час штормів.
                                Ці харизматичні птахи ковзають на животах по льоду і можуть занурюватися на вражаючі глибини під час полювання.""");

        addEntry("eagle")
                .addTranslation("en_us", "Golden Eagle", "Aquila chrysaetos", "Modern",
                        """
                                The golden eagle is one of the largest birds of prey in the world, known for its powerful build and impressive hunting skills.
                                Found in mountain biomes, eagles soar on thermal currents searching for prey below. They have exceptional eyesight and can spot prey from great heights.
                                Eagles are protected species in many regions. Killing them may result in negative effects. They hunt small animals and will occasionally attack livestock.
                                These magnificent raptors mate for life and build massive nests called eyries on cliff faces and tall trees.""")
                .addTranslation("uk_ua", "Беркут", "Aquila chrysaetos", "Сучасність",
                        """
                                Беркут — один з найбільших хижих птахів у світі, відомий своєю потужною будовою та вражаючими мисливськими навичками.
                                Беркути, що живуть у гірських біомах, ширяють на термальних потоках, шукаючи здобич внизу. Вони мають винятковий зір і можуть помітити здобич з великої висоти.
                                Беркути є захищеним видом у багатьох регіонах. Їх вбивство може призвести до негативних наслідків. Вони полюють на дрібних тварин і іноді атакують худобу.
                                Ці величні хижаки створюють пару на все життя і будують масивні гнізда, які називаються еріями, на скелях і високих деревах.""");

        addEntry("saola")
                .addTranslation("en_us", "Saola", "Pseudoryx nghetinhensis", "Modern (Discovered 1992)",
                        """
                                The saola is one of the world's rarest mammals, sometimes called the "Asian unicorn" due to its long, straight horns and elusive nature.
                                Found only in dense jungle biomes, saola are extremely shy and rarely seen. They are peaceful herbivores that feed on forest vegetation.
                                Saola cannot be tamed but will flee when approached. They are critically endangered and should be protected rather than hunted.
                                These mysterious animals were unknown to science until 1992, making them one of the largest land mammals discovered in the 20th century.""")
                .addTranslation("uk_ua", "Саола", "Pseudoryx nghetinhensis", "Сучасність (Відкрита 1992)",
                        """
                                Саола — один з найрідкісніших ссавців у світі, іноді називається "азіатським єдинорогом" через свої довгі прямі роги та невловиму природу.
                                Саоли, що живуть лише в густих джунглевих біомах, надзвичайно сором'язливі і рідко їх бачать. Вони мирні травоїдні, які харчуються лісовою рослинністю.
                                Саолу не можна приручити, але вони втікають при наближенні. Вони знаходяться під критичною загрозою зникнення і повинні бути захищені, а не полюваними.
                                Ці таємничі тварини були невідомі науці до 1992 року, що робить їх одними з найбільших наземних ссавців, відкритих у 20 столітті.""");

        addEntry("pheasant")
                .addTranslation("en_us", "Pheasant", "Phasianus colchicus", "Modern",
                        """
                                Pheasants are colorful game birds native to Asia but now found worldwide. Males have brilliant plumage while females are more camouflaged.
                                Found in forest and plains biomes, pheasants forage on the ground for seeds, insects, and berries. They can be bred using seeds.
                                Pheasants provide feathers and meat when hunted. Males make loud calls during breeding season and perform elaborate courtship displays.
                                These birds prefer to run rather than fly when disturbed, but can burst into short, powerful flights when necessary.""")
                .addTranslation("uk_ua", "Фазан", "Phasianus colchicus", "Сучасність",
                        """
                                Фазани — барвисті мисливські птахи, що походять з Азії, але тепер зустрічаються по всьому світу. Самці мають блискуче оперення, тоді як самки більш замасковані.
                                Фазани, що живуть у лісах і на рівнинах, шукають їжу на землі, полюючи на насіння, комах і ягоди. Їх можна розводити за допомогою насіння.
                                Фазани дають пір'я та м'ясо при полюванні. Самці видають гучні звуки під час сезону розмноження та виконують складні шлюбні танці.
                                Ці птахи віддають перевагу бігу, а не польоту, коли їх турбують, але можуть злітати на короткі, потужні польоти, коли це необхідно.""");

        addEntry("domestic_goat")
                .addTranslation("en_us", "Domestic Goat", "Capra hircus", "Modern (Domesticated ~10,000 years ago)",
                        """
                                Domestic goats are hardy livestock animals raised for milk, meat, and fiber. They are known for their curious nature and ability to climb steep terrain.
                                Found in various biomes, domestic goats are browsers that eat leaves, twigs, and shrubs rather than grass. They can be bred using wheat.
                                Goats provide milk when milked with a bucket, and meat when slaughtered. Some varieties produce wool that can be sheared.
                                These intelligent animals are more closely related to wild ibex than to mountain goats, and have been essential to human agriculture for millennia.""")
                .addTranslation("uk_ua", "Домашня коза", "Capra hircus", "Сучасність (Одомашнена ~10,000 років тому)",
                        """
                                Домашні кози — витривалі свійські тварини, що розводяться для молока, м'яса та волокна. Вони відомі своєю цікавою природою та здатністю лазити по крутій місцевості.
                                Домашні кози, що живуть у різних біомах, є браузерами, які їдять листя, гілки та кущі, а не траву. Їх можна розводити за допомогою пшениці.
                                Кози дають молоко при доїнні відром і м'ясо при забої. Деякі сорти виробляють вовну, яку можна стригти.
                                Ці розумні тварини більш тісно пов'язані з дикими козлами, ніж з гірськими козами, і були важливими для людського сільського господарства протягом тисячоліть.""");

        addEntry("goat")
                .addTranslation("en_us", "Mountain Goat", "Oreamnos americanus", "Modern",
                        """
                                Mountain goats are sure-footed ungulates native to mountainous regions. Despite their name, they are not true goats but are more closely related to antelopes.
                                Found in mountain biomes, these hardy animals can navigate steep cliffs with ease. They will ram into players and mobs that get too close.
                                Mountain goats can be bred using wheat. They occasionally drop horns when ramming into solid blocks, which can be used to craft instruments.
                                Their thick white coats protect them from harsh mountain weather, and their specialized hooves provide excellent grip on rocky surfaces.""")
                .addTranslation("uk_ua", "Гірська коза", "Oreamnos americanus", "Сучасність",
                        """
                                Гірські кози — впевнені копитні тварини, що живуть у гірських регіонах. Незважаючи на свою назву, вони не є справжніми козами, а більш тісно пов'язані з антилопами.
                                Ці витривалі тварини, що живуть у гірських біомах, можуть легко пересуватися крутими скелями. Вони таранять гравців і мобів, які підходять занадто близько.
                                Гірських кіз можна розводити за допомогою пшениці. Іноді вони випускають роги, коли таранять тверді блоки, які можна використовувати для виготовлення інструментів.
                                Їхнє густе біле хутро захищає їх від суворої гірської погоди, а спеціалізовані копита забезпечують чудове зчеплення на скелястих поверхнях.""");
    }

    /**
     * Helper method to create entry builders
     */
    private BestiaryEntryBuilder addEntry(String entityId) {
        BestiaryEntryBuilder builder = new BestiaryEntryBuilder(entityId);
        entries.add(builder);
        return builder;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        addEntries();

        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (BestiaryEntryBuilder entry : entries) {
            for (Map.Entry<String, BestiaryEntryBuilder.Translation> translation : entry.getTranslations().entrySet()) {
                String language = translation.getKey();
                String content = translation.getValue().toFileContent();

                Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                        .resolve(AncientHorizons.MOD_ID)
                        .resolve("bestiary")
                        .resolve(language)
                        .resolve(entry.getEntityId() + ".txt");

                String normalized = Normalizer.normalize(content, Normalizer.Form.NFC);

                futures.add(
                        DataProvider.saveStable(cache, new JsonPrimitive(normalized), path)
                );
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return "Bestiary Entries";
    }
}