-- V006__fix_diacritic_encoding.sql
--
-- Fix incorrectly encoded Arabic diacritics in the exercise table.
--
-- Problem: 86 rows contain U+06EA (ARABIC EMPTY CENTRE LOW STOP ۪) where a
--          standard kasra U+0650 (ِ) should be used. One row contains the
--          U+FEFC presentation-form ligature (ﻼ) instead of plain لا.
--
-- Root cause: A Quranic text editor inserted the Uthmani-script annotation
--             character U+06EA before ي/ى to represent a long "i" vowel,
--             instead of the standard combining kasra U+0650.
--
-- Fix: Replace every U+06EA with U+0650 and every U+FEFC with لا.

UPDATE exercise SET value = 'فِيهِ' WHERE id = '7f288cc8-9a6a-4d17-9764-7f22d16db51c';
UPDATE exercise SET value = 'قِيلَ' WHERE id = '4a36d5fd-2e3b-4cdf-b212-4747962afe35';
UPDATE exercise SET value = 'هٰذِهِ' WHERE id = '96eafd53-f1de-491a-8f8f-833131ae09ce';
UPDATE exercise SET value = 'فَرِحِينَ' WHERE id = 'e1183bcc-f3f7-4a90-b34a-e6b4642c907c';
UPDATE exercise SET value = 'كَافِرِينَ' WHERE id = '46937d67-9f90-4cf0-a688-867b3dd4f149';
UPDATE exercise SET value = 'رُوحِى' WHERE id = 'b24dab25-b208-4ddb-b43d-147d9d8dffd5';
UPDATE exercise SET value = 'لا لا لا' WHERE id = '7099c9c6-37c5-4e66-83f8-e5ec06992b32';
UPDATE exercise SET value = 'قُلْ فِيهِمَا' WHERE id = 'c21d3b80-45b9-4371-a333-6113a64fc667';
UPDATE exercise SET value = 'ذٰلِكُمْ اِصْرِى' WHERE id = '4dbc0b94-5b4e-4330-864a-1d53aff7ee79';
UPDATE exercise SET value = 'دِينٍ' WHERE id = '6d92b20d-c462-4cc5-bfd3-9c2d426f7d22';
UPDATE exercise SET value = 'عَذَابٍ غَلِيظٍ' WHERE id = '6bef1c93-d730-4c23-a855-ff80f0d85b1f';
UPDATE exercise SET value = 'حَمِيمٌ' WHERE id = '68df2e2b-d1b6-4341-8e57-42a7f6dffd45';
UPDATE exercise SET value = 'عَزِيزٌ حَكِيمٌ' WHERE id = 'a84f9670-73cb-45dc-8840-4f8aeb84b789';
UPDATE exercise SET value = 'فِ‍‍‍يهِ' WHERE id = '8c836a53-768c-4946-ac46-7d71441a9e2e';
UPDATE exercise SET value = 'نُوحِ‍‍‍يهِ' WHERE id = 'aa12e250-87d4-4d37-849c-a48db4b1dc2a';
UPDATE exercise SET value = 'فِى هٰذِهِ الدُّنْيَا' WHERE id = '4a959ed1-642f-4593-86a9-d06cc3c6a8c9';
UPDATE exercise SET value = 'كَشَجَ‍‍‍رَةٍ خَبِيثَةٍ' WHERE id = '5f063c27-37fc-421c-a9ca-9cd7055e21eb';
UPDATE exercise SET value = 'اَلْاِنْسَانُ   اِنَّ الْاِنْسَانَ لَفِي خُسْرٍۙ' WHERE id = 'b2e8f698-1b72-4600-b9ce-3401dc5003ee';
UPDATE exercise SET value = 'اَبْرَارٌ اَلْاَبْرَارُ   اِنَّ الْاَبْرَارَ لَفِي نَعِيمٍۚ' WHERE id = 'ddb975b0-c7aa-431c-9e43-5f5d825c64d5';
UPDATE exercise SET value = 'فَتَبَارَكَ اللّٰهُ اَحْسَنُ الْخَالِقِينَۜ' WHERE id = '1f02615f-0a6a-4b34-a7df-2dd54088f1a3';
UPDATE exercise SET value = 'اِى' WHERE id = '3b02e7c3-e755-43b3-918c-2d917eec482f';
UPDATE exercise SET value = 'بِى' WHERE id = '78f491ee-c5cd-4efc-b3ea-5bef6b5151ae';
UPDATE exercise SET value = 'تِى' WHERE id = '750a9ef1-5fc7-4eb5-947a-2efba9ea9abf';
UPDATE exercise SET value = 'ثِى' WHERE id = 'b8ddcb01-a0ab-4171-a70a-a59b8edef3df';
UPDATE exercise SET value = 'فِيهِ' WHERE id = '6e6893da-09e1-475c-85ee-c035bd419839';
UPDATE exercise SET value = 'قِيلَ' WHERE id = 'ed76d398-381f-47d6-b990-ed6eb5f03b3a';
UPDATE exercise SET value = 'حِينَ' WHERE id = 'da9b74e8-95f0-44da-a8a6-d9b329e5369e';
UPDATE exercise SET value = 'عِضِينَ' WHERE id = 'a3f28e08-9a1c-4343-8664-d131fcf65e23';
UPDATE exercise SET value = 'بِبَنِيهِ' WHERE id = 'eec453ec-54ad-47bb-a988-1b5b59ecf6c1';
UPDATE exercise SET value = 'ثَلٰثِينَ' WHERE id = 'fccd4da7-d6c3-410d-9272-8d19131f87eb';
UPDATE exercise SET value = 'سَبِيلِى' WHERE id = 'cbfe5922-475b-4fe7-9241-91688d06da06';
UPDATE exercise SET value = 'قَ‍‍‍لِي‍‍لًا' WHERE id = '6cb2db6d-4ac8-4564-ba17-0c2dd61c8f0d';
UPDATE exercise SET value = 'جَ‍‍‍دِي‍‍دًا' WHERE id = 'a746a722-c9bf-4b2e-b45b-995d36f07ed7';
UPDATE exercise SET value = 'تَسْ‍‍‍بِي‍‍حَهُمْ' WHERE id = 'b7277f9c-b518-4c03-8011-0211ca3869c2';
UPDATE exercise SET value = 'وَسَٓاءَتْ مَصِيرًا' WHERE id = '505eace5-4bac-4c71-bd55-6d6089d90e70';
UPDATE exercise SET value = 'اَنْ تَ‍‍‍بُٓوأَبِاِثْمِى' WHERE id = '44ee4778-069e-412d-b73b-8fdd1256f7e5';
UPDATE exercise SET value = 'سِٓئَبِهِمْ' WHERE id = '605e1b54-4e4c-4de7-b00b-5764b51881da';
UPDATE exercise SET value = 'حَتّٰى تَ‍‍‍فِۤئَ' WHERE id = '86059c38-6972-4773-b6f4-5cce1ef556ab';
UPDATE exercise SET value = 'زَيْتُهَا يُ‍‍‍ضِٓئُ' WHERE id = 'e83464da-10cc-4daf-bd8c-eb0b7afa7588';
UPDATE exercise SET value = 'فِٓى اَيْدِيكُمْ' WHERE id = '43c75018-065f-4885-af5f-a4d61836ad18';
UPDATE exercise SET value = 'اِنِّٓى اَرَانِى' WHERE id = 'b4129270-b127-479c-9582-6fb5e86b81af';
UPDATE exercise SET value = 'وَيَسِّرْلِٓى اَمْرِى' WHERE id = '805d35b3-ea1d-4f6c-9705-e7cdb3b5e5c1';
UPDATE exercise SET value = 'تَاْمُ‍‍‍رُٓونِّى' WHERE id = 'd0a908e5-24de-4aa8-89bd-e1699896e994';
UPDATE exercise SET value = 'حِينَ تَ‍‍‍قُومُ' WHERE id = '50894b13-1fc6-401b-a6fa-cbb09f5f35ff';
UPDATE exercise SET value = 'بِقَلْبٍ سَ‍‍‍لِيمٍ' WHERE id = 'abcfd2f5-1aa2-48e3-8c3e-78933e0ebc2d';
UPDATE exercise SET value = 'بِرَبِّ الْعٰلَ‍‍‍مِينَ' WHERE id = 'c2e2283e-b41a-4894-9f15-9dee5a4aef8a';
UPDATE exercise SET value = 'مَعَهُۤ اِلَّا قَ‍‍‍لِيلٌ' WHERE id = 'b18fbf25-60ae-41cd-84eb-799fb9181587';
UPDATE exercise SET value = 'مِ‍‍‍نْ دُونِهِ' WHERE id = 'f02bfec6-d83c-430a-9d7d-2e55e57d39f0';
UPDATE exercise SET value = 'زَوْجٍ كَ‍‍رِيمٍ' WHERE id = 'a265356e-c59e-489d-a7c0-b9593d4bf6a0';
UPDATE exercise SET value = 'عَلِي‍‍‍مٌ حَ‍‍كِيمٌ' WHERE id = '4738b3b9-d5e0-482a-aed8-62b2448ff960';
UPDATE exercise SET value = 'وَلَامِ‍‍‍نْ خَ‍‍لْفِهِ' WHERE id = '9725e200-0621-438a-861f-ad6d11ee0f39';
UPDATE exercise SET value = 'خُلُ‍‍‍قٍ عَ‍‍ظِيمٍ' WHERE id = '8b3857ba-c8ba-4254-8a70-ad51340b38a9';
UPDATE exercise SET value = 'مِيثٰ‍‍‍قًاغَ‍‍لِيظًا' WHERE id = '4660a9cc-e793-40e3-8cef-9a1b83277af3';
UPDATE exercise SET value = 'غَفُورٌ رَحِيمٌ' WHERE id = '5da71497-6eb4-4d6a-88a5-003482c0b2cd';
UPDATE exercise SET value = 'غَفُورُ رَّحِيمٌ' WHERE id = '6668bc07-de1f-4eed-8f9f-aded64a1680b';
UPDATE exercise SET value = 'هُ‍‍‍دًى لِ‍‍لْمُتَّقِينَ' WHERE id = '3cc19773-c44b-4c0e-bf27-2ed36c150d79';
UPDATE exercise SET value = 'هُدَلِّ‍‍لْمُتَّقِينَ' WHERE id = 'dbe29152-6279-4c1d-9f7b-7682465c48f6';
UPDATE exercise SET value = 'وَكُ‍‍‍نْ مِ‍‍نَ الشّٰكِرِينَ' WHERE id = 'f46764c8-ad7f-4d07-8b72-5402b32f59a4';
UPDATE exercise SET value = 'وَكُمِّ‍‍نَ الشّٰكِرِينَ' WHERE id = '6688eee9-ca7a-4802-a8bb-f049c7739361';
UPDATE exercise SET value = 'عِفْرِي‍‍‍تٌ مِ‍‍نَ الْجِنِّ' WHERE id = '024608db-be96-4edf-b5ea-19e9a6f37d47';
UPDATE exercise SET value = 'عِفْرِي‍‍‍تُ مِّ‍‍نَ الْجِنِّ' WHERE id = '03b3ac17-c537-4966-8bf8-575cc5d6c162';
UPDATE exercise SET value = 'اِنْ نَ‍‍سِينَا' WHERE id = '9c4db4e2-cc2d-486d-b5a3-dd22b7917a87';
UPDATE exercise SET value = 'اِنَّ‍‍سِينَا' WHERE id = '37bd9615-5628-4873-ba29-d2421fa231ec';
UPDATE exercise SET value = 'وَمَا يَ‍‍‍نْبَ‍‍غِى' WHERE id = '00b857d0-ac6b-4172-91fd-a9ba56de37bc';
UPDATE exercise SET value = 'وَمَا يَ‍‍‍مْبَ‍‍غِى' WHERE id = '15274fd9-aed2-4135-acea-e4d36240b41c';
UPDATE exercise SET value = 'شَدِي‍‍‍دٌ بِ‍‍مَا' WHERE id = 'ee89581c-8946-4964-b21d-54bab06aaa39';
UPDATE exercise SET value = 'شَدِيدُمْ بِ‍‍مَا' WHERE id = 'bd1889b3-001d-4537-862a-471a8e5b8316';
UPDATE exercise SET value = 'جَمِي‍‍‍عًا بَ‍‍عْضُكُمْ' WHERE id = '17856e74-7daa-4ad3-8a8d-82ce47e4bfec';
UPDATE exercise SET value = 'جَمِيعَ‍‍‍مْ بَ‍‍عْضُكُمْ' WHERE id = '2a3d72a0-06d2-48a2-82e9-e64ebf39e229';
UPDATE exercise SET value = 'اَمْ بِ‍‍هِ جِنَّةٌ' WHERE id = '41c96a96-1d54-4b0f-aa40-a2da3c051698';
UPDATE exercise SET value = 'وَلَسْتُ‍‍‍مْ بِ‍‍اٰخِذِيهِ' WHERE id = 'ad611ab8-4fc7-446a-8459-8a2c0a99b414';
UPDATE exercise SET value = 'تَرْمِيهِ‍‍‍مْ بِ‍‍حِجَارَةٍ' WHERE id = 'b2a73c68-4df5-476d-9880-87ee5e446648';
UPDATE exercise SET value = 'نَ‍‍‍جْ‍‍زِى الْمُ‍‍‍جْ‍‍رِمِينَ' WHERE id = '90c55e0a-0a49-4094-bc7a-4255e7961b38';
UPDATE exercise SET value = 'اَفَنَ‍‍‍جْ‍‍عَلُ الْمُسْلِمِينَ' WHERE id = 'fdba5eeb-1cc5-4871-a6fb-0ca27f5209a9';
UPDATE exercise SET value = 'يُحِبُّ الْمُ‍‍‍قْ‍‍سِطِينَ' WHERE id = '077a644f-8998-4e14-a730-dd67aeafcf6b';
UPDATE exercise SET value = 'اَلِفْ لَامِّيمْ' WHERE id = 'cf513c45-2ea8-4012-8648-ba0b559170ce';
UPDATE exercise SET value = 'اَلِفْ لَامِّيمْ صَادْ' WHERE id = '5668015f-5307-4d43-8c30-dfbc8d3d56e3';
UPDATE exercise SET value = 'اَلِفْ لَامِّيمْ رَا' WHERE id = '20677a6b-bd60-4e2d-aeb0-1f0b3de2585f';
UPDATE exercise SET value = 'طَاسِيمِّيمْ' WHERE id = 'b1e0637c-29e1-4bff-b54e-8bf3853ced89';
UPDATE exercise SET value = 'يَاسِينْ' WHERE id = '1df4c2f3-0000-4cb8-a16f-8f4bc4c066d2';
UPDATE exercise SET value = 'حَامِيمْ' WHERE id = '35cd80f9-1f29-4d99-a197-f752956dd7a4';
UPDATE exercise SET value = 'عَيْنْ سِينْ قَافْ' WHERE id = '18502767-c572-4770-b160-e9ac2c41cb3b';
UPDATE exercise SET value = 'بَعْ‍‍‍دِهِ' WHERE id = '5b18f6ca-628d-4323-a924-fa48a1aafca3';
UPDATE exercise SET value = 'وَكُتُ‍‍‍بِهِ' WHERE id = '998d6203-f5ba-4bf4-b3bf-e854edabd99b';
UPDATE exercise SET value = 'وَرُسُ‍‍‍لِهِ' WHERE id = '51aa2de4-10dc-44e5-9561-def4731ff60f';
UPDATE exercise SET value = 'فِ‍‍‍يهِ' WHERE id = '1ab89a0e-cc27-4b4a-b53f-c69743c6e33f';
UPDATE exercise SET value = 'سَبِي‍‍‍لِ اللّٰهِ ' WHERE id = 'c9a9cfa1-62a8-4e90-9f15-3be8bf77fd86';
