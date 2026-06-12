import unittest
from decimal import Decimal

import import_foods_from_xlsx as importer


class ImportFoodsFromXlsxTest(unittest.TestCase):

    def test_food_values_parse_korean_headers_and_piece_weight(self):
        header = [
            "\uC2DD\uD488\uCF54\uB4DC",
            "\uC2DD\uD488\uBA85",
            "\uC2DD\uD488\uB300\uBD84\uB958\uBA85",
            "\uC601\uC591\uC131\uBD84\uD568\uB7C9\uAE30\uC900\uB7C9",
            "\uC2DD\uD488\uC911\uB7C9",
            "\uC5D0\uB108\uC9C0(kcal)",
            "\uB2E8\uBC31\uC9C8(g)",
            "\uD0C4\uC218\uD654\uBB3C(g)",
            "\uC9C0\uBC29(g)",
            "\uB2F9\uB958(g)",
            "\uB098\uD2B8\uB968(mg)",
            "\uC5C5\uCCB4\uBA85",
            "\uCD9C\uCC98\uBA85",
        ]
        row = [
            "F-001",
            "\uAD6C\uC6B4\uACC4\uB780 (3\uAC1C\uC785)",
            "\uB09C\uB958",
            "100g",
            "180g",
            "150",
            "12.3",
            "1.1",
            "10.2",
            "0.4",
            "120",
            "\uD14C\uC2A4\uD2B8\uBE0C\uB79C\uB4DC",
            "\uD14C\uC2A4\uD2B8\uCD9C\uCC98",
        ]

        columns = importer.find_columns(header)
        values = importer.food_values(row, columns)

        self.assertEqual(values["external_food_code"], "F-001")
        self.assertEqual(values["name"], "\uAD6C\uC6B4\uACC4\uB780 (3\uAC1C\uC785)")
        self.assertEqual(values["category"], "\uB09C\uB958")
        self.assertEqual(values["nutrition_basis_amount"], Decimal("100"))
        self.assertEqual(values["nutrition_basis_unit"], "g")
        self.assertEqual(values["serving_amount"], Decimal("180"))
        self.assertEqual(values["serving_unit"], "g")
        self.assertEqual(values["gram_per_piece"], Decimal("60"))
        self.assertEqual(values["calories"], Decimal("150"))
        self.assertEqual(values["protein_g"], Decimal("12.3"))
        self.assertEqual(values["brand"], "\uD14C\uC2A4\uD2B8\uBE0C\uB79C\uB4DC")
        self.assertEqual(values["source"], "\uD14C\uC2A4\uD2B8\uCD9C\uCC98")

    def test_insert_statement_escapes_strings_and_uses_upsert(self):
        values = {column: None for column in importer.INSERT_COLUMNS}
        values.update({
            "external_food_code": "F-002",
            "name": "O'Brien",
            "nutrition_basis_amount": Decimal("100"),
            "nutrition_basis_unit": "g",
            "calories": Decimal("10"),
            "protein_g": Decimal("1"),
            "carbs_g": Decimal("2"),
            "fat_g": Decimal("3"),
            "sugar_g": Decimal("4"),
            "sodium_mg": Decimal("5"),
            "fiber_g": Decimal("6"),
            "iron_mg": Decimal("7"),
            "phosphorus_mg": Decimal("8"),
            "potassium_mg": Decimal("9"),
            "vitamin_a_ug_rae": Decimal("0"),
            "beta_carotene_ug": Decimal("0"),
            "retinol_ug": Decimal("0"),
        })

        statement = importer.insert_statement(values)

        self.assertIn("INSERT INTO foods", statement)
        self.assertIn("'O''Brien'", statement)
        self.assertIn("ON DUPLICATE KEY UPDATE", statement)
        self.assertIn("name = VALUES(name)", statement)


if __name__ == "__main__":
    unittest.main()
